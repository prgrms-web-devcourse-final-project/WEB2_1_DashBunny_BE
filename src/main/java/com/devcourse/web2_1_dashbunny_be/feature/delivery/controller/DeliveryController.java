package com.devcourse.web2_1_dashbunny_be.feature.delivery.controller;

import com.devcourse.web2_1_dashbunny_be.domain.delivery.DeliveryHistory;
import com.devcourse.web2_1_dashbunny_be.domain.delivery.DeliveryRequests;
import com.devcourse.web2_1_dashbunny_be.domain.delivery.role.DeliveryWorkerStatus;
import com.devcourse.web2_1_dashbunny_be.domain.user.User;
import com.devcourse.web2_1_dashbunny_be.feature.delivery.dto.DeliveryRequestsDto;
import com.devcourse.web2_1_dashbunny_be.feature.delivery.dto.DeliveryWorkerUpdateAddressRequestDto;
import com.devcourse.web2_1_dashbunny_be.feature.delivery.service.DeliveryService;
import com.devcourse.web2_1_dashbunny_be.feature.delivery.service.SseService;
import com.devcourse.web2_1_dashbunny_be.feature.order.repository.OrdersRepository;
import com.devcourse.web2_1_dashbunny_be.feature.user.repository.UserRepository;
import com.devcourse.web2_1_dashbunny_be.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
public class DeliveryController {

	private final UserService userService;
	private final DeliveryService deliveryService;
	private final SseService sseService;


	/**
	 * 배달원의 주소를 업데이트하는 엔드포인트입니다.
	 *
	 * @param authorizationHeader 클라이언트의 Authorization 헤더 (JWT 토큰 포함)
	 * @param deliveryWorkerUpdateAddressRequestDto 주소 업데이트를 위한 요청 데이터
	 * @return 주소 업데이트 결과에 따른 HTTP 응답
	 */
	@PostMapping("/worker/update/address")
	public ResponseEntity<?> updateDeliveryWorkerAddress(
					@RequestHeader("Authorization") String authorizationHeader,
					@RequestBody DeliveryWorkerUpdateAddressRequestDto deliveryWorkerUpdateAddressRequestDto)
	{
		// Authorization 헤더에서 현재 사용자 정보를 추출
		User currentUser = userService.getCurrentUser(authorizationHeader);
		log.info("updateDeliveryWorkerAddress - Current userId: {}, Request userId: {}",
						currentUser.getUserId(), deliveryWorkerUpdateAddressRequestDto.getUserId());

		// 요청된 사용자 ID와 현재 사용자 ID가 일치하는지 확인
		if (!currentUser.getUserId().equals(deliveryWorkerUpdateAddressRequestDto.getUserId())) {
			log.warn("사용자 ID가 일치하지 않습니다.: currentUserId={}, requestUserId={}",
							currentUser.getUserId(), deliveryWorkerUpdateAddressRequestDto.getUserId());
			// 일치하지 않을 경우 403 Forbidden 응답 반환
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("사용자 ID가 일치하지 않습니다.");
		}
		// 주소 업데이트를 수행하고 성공 시 200 OK , user 반환
		return ResponseEntity.ok(deliveryService.updateDeliveryWorkerAddress(deliveryWorkerUpdateAddressRequestDto, currentUser));
	}

	/**
	 * 배달원의 배달 가능 여부를 토글하는 엔드포인트입니다.
	 *
	 * @param authorizationHeader 클라이언트의 Authorization 헤더 (JWT 토큰 포함)
	 * @return 업데이트된 상태에 따른 HTTP 응답
	 */
	@PostMapping("/worker/status/toggle")
	public ResponseEntity<DeliveryWorkerStatus> toggleDeliveryStatus(@RequestHeader("Authorization") String authorizationHeader) {
		// Authorization 헤더에서 현재 사용자 정보를 추출
		User currentUser = userService.getCurrentUser(authorizationHeader);
		log.info("toggleWorkerStatus - Current userId: {}", currentUser.getUserId());

		currentUser = deliveryService.toggleDeliveryStatus(currentUser);

		log.info("배달 상태 업데이트: {}", currentUser.getDeliveryStatus());
		return ResponseEntity.ok(currentUser.getDeliveryStatus());
	}

	/**
	 * 가게에서 온 배달 요청을 처리하는 엔드포인트
	 * @param deliveryRequestsDto 주문 정보
	 * @return 생성된 주문 정보
	 */
	@PostMapping("/request")
	public DeliveryRequests deliveryRequest (@RequestBody DeliveryRequestsDto deliveryRequestsDto) {
		// 주문 정보 저장
		DeliveryRequests savedDeliveryRequests = deliveryService.saveDeliveryRequests(deliveryRequestsDto);

		// 조건에 맞는 배달원 찾기
		List<User> eligibleDeliveryWorkers = deliveryService.deliveryWorkerWithInARadius(savedDeliveryRequests);
		log.info("deliveryRequest - eligibleDeliveryWorkers : {}", eligibleDeliveryWorkers);
		// 배달원에게 SSE로 알림 전송
		for (User deliveryWorker : eligibleDeliveryWorkers) {
			log.info("for문 안 - deliveryWorker.getUserId : {}", deliveryWorker.getUserId());
			sseService.notifyDeliveryAssignment
							(deliveryWorker.getUserId(), deliveryService.convertToDeliveryOrderNotificationDto(savedDeliveryRequests));
		}

		return savedDeliveryRequests;
	}

	/**
	 * 배달 요청을 수락하는 엔드포인트
	 * @param authorizationHeader 클라이언트의 Authorization(JWT) 정보
	 * @param deliveryRequests 배달 요청 정보를 포함하는 객체
	 * @return 저장된 배달 히스토리 정보
	 */
	@PostMapping("/accept-delivery")
	public ResponseEntity<?> acceptDeliveryRequest(@RequestHeader("Authorization") String authorizationHeader,
																								 @RequestBody DeliveryRequests deliveryRequests){
		log.info("acceptDeliveryRequest - deliveryRequests : {}", deliveryRequests);
		// DeliveryRequests 에서 가져온 후 DeliveryHistory 저장
		DeliveryHistory saveDeliveryHistory =
						deliveryService.acceptDeliveryRequest(authorizationHeader, deliveryRequests);
		log.info("saveDeliveryHistory : {}", saveDeliveryHistory);
		return ResponseEntity.ok(saveDeliveryHistory);
	}

}

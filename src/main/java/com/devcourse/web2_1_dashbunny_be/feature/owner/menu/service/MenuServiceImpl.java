package com.devcourse.web2_1_dashbunny_be.feature.owner.menu.service;

import com.devcourse.web2_1_dashbunny_be.domain.owner.MenuManagement;
import com.devcourse.web2_1_dashbunny_be.domain.owner.StoreManagement;
import com.devcourse.web2_1_dashbunny_be.feature.owner.common.Validator;
import com.devcourse.web2_1_dashbunny_be.feature.owner.dto.menu.*;
import com.devcourse.web2_1_dashbunny_be.feature.owner.store.repository.StoreManagementRepository;
import com.devcourse.web2_1_dashbunny_be.feature.owner.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final Validator validator;
    private final StoreManagementRepository storeManagementRepository;
    private final MenuRepository menuRepository;

    //전체 메뉴 조회(메뉴 관리 1페이지 목록)
    @Override
    public List<MenuManagement> findStoreAllMenu(String storeId) {
        StoreManagement store = validator.validateStoreId(storeId);
        return menuRepository.findAllByStoreId(storeId);
    }

    @Override
    public List<MenuManagement> findGroupMenu(String groupId) {
        return List.of();
    }

    @Override
    public List<MenuManagement> findSearchMenuName(String menuName) {
        return List.of();
    }

    @Override
    public void create(Long storeId, MenuManagement menu) {

    }

    @Override
    public void updateAll(Long menuId, UpdateMenuRequestDto updateMenuRequestDTO) {

    }

    @Override
    public void updateActionIsSoldOut(Long menuId, UpdateActionRequestDto actionRequestDTO) {

    }

    @Override
    public void updateImage(Long menuId, UpdateMenuImageRequestDto imageUrlDTO) {

    }

    @Override
    public void updateIsSoldOut(Long menuId, UpdateSoldOutRequestDto updateSoldOutRequestDTO) {

    }

    @Override
    public void delete(UpdateActionRequestDto actionRequestDTO) {

    }

    @Override
    public MenuWithMenuGroupResponseDto MenuWithGroups(Long meneId) {
        return null;
    }
}
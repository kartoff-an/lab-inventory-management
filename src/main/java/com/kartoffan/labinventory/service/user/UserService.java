package com.kartoffan.labinventory.service.user;

import java.util.List;
import java.util.UUID;

import com.kartoffan.labinventory.dto.user.UpdateUserRequest;
import com.kartoffan.labinventory.model.User;

public interface UserService {

  User getById(UUID userId);

  List<User> getAll();

  User update(UUID userId, UpdateUserRequest request);

  void deactivate(UUID userId);

  void recover(UUID userId);

  User getCurrentUser();

}

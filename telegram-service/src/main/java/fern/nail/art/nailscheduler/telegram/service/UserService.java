package fern.nail.art.nailscheduler.telegram.service;


import fern.nail.art.nailscheduler.telegram.model.User;

public interface UserService {
    User getUserByChatId(Long userId);
}
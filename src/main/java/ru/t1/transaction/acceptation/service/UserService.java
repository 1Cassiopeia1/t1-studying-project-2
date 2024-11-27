package ru.t1.transaction.acceptation.service;

import ru.t1.transaction.acceptation.model.User;

public interface UserService {

    /**
     * Сохранение пользователя
     *
     * @return сохраненный пользователь
     */
    User save(User user);

    /**
     * Создание пользователя
     *
     * @return созданный пользователь
     */
    User create(User user);

    /**
     * Получение пользователя по имени пользователя
     *
     * @return пользователь
     */
    User getByUsername(String username);

    /**
     * Проверка существования пользователя по имени
     *
     * @return флаг существования
     */
    boolean existsByUsername(String username);
}

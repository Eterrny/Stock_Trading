package ru.ssu.springproject.stocks_trading.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ssu.springproject.stocks_trading.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
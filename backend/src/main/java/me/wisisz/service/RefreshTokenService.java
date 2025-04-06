package me.wisisz.service;

import me.wisisz.model.RefreshToken;
import me.wisisz.model.Person;
import me.wisisz.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public List<RefreshToken> getAllRefreshToken() {
        return refreshTokenRepository.findAll();
    }

    public Optional<RefreshToken> getRefreshTokenById(Integer tokenId) {
        return refreshTokenRepository.findById(tokenId);
    }

    public Optional<RefreshToken> getRefreshTokenByPerson(Person person) {
        return refreshTokenRepository.findByPerson(person);
    }

    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}

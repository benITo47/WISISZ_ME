package me.wisisz.service;

import me.wisisz.model.RefreshToken;
import me.wisisz.model.Person;
import me.wisisz.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.OffsetDateTime;

@Service
public class RefreshTokenService {

    @Autowired
    private PersonService personService;

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

    public void saveRefreshTokenToDatabase(String refreshToken, String emailAddr) throws Exception {
        Optional<Person> personOptional = personService.getPersonByEmail(emailAddr);

        if (!personOptional.isPresent()) {
            throw new Exception("Invalid person");
        }

        Person person = personOptional.get();

        Optional<RefreshToken> refreshTokenOptional = getRefreshTokenByPerson(person);

        if (refreshTokenOptional.isPresent()) {

            RefreshToken modelToken = refreshTokenOptional.get();

            modelToken.setToken(refreshToken);
            modelToken.setIssuedAt(OffsetDateTime.now());
            modelToken.setExpiresAt(OffsetDateTime.now().plusDays(1));
            modelToken.setIsRevoked(false);

            saveRefreshToken(modelToken);

        } else {

            RefreshToken modelToken = new RefreshToken();
            modelToken.setPerson(person);
            modelToken.setToken(refreshToken);
            modelToken.setIssuedAt(OffsetDateTime.now());
            modelToken.setExpiresAt(OffsetDateTime.now().plusDays(1));
            modelToken.setIsRevoked(false);

            saveRefreshToken(modelToken);
        }
    }

    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}

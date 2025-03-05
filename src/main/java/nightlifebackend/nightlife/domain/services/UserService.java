package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.User;
import nightlifebackend.nightlife.domain.persistence_ports.UserPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserPersistence userPersistence;

    @Autowired
    public UserService(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    public User create(User user) {
        //user.setRegistrationDate(LocalDate.now());
        //this.assertBarcodeNotExist(article.getBarcode());
        return this.userPersistence.create(user);
    }
}

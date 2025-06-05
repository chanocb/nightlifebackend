package nightlifebackend.nightlife.adapters.postgresql.daos;

import lombok.extern.log4j.Log4j2;
import nightlifebackend.nightlife.adapters.postgresql.entities.*;
import nightlifebackend.nightlife.domain.models.DayOfWeek;
import nightlifebackend.nightlife.domain.models.Music;
import nightlifebackend.nightlife.domain.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Log4j2
@Repository
public class UserSeederDev {
    private final DatabaseStarting databaseStarting;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;
    private final ReviewRepository reviewRepository;

    private final EventRepository eventRepository;
    private final AccessTypeRepository accessTypeRepository;
    private final ReservationRepository reservationRepository;
    private String PASSWORD;

    @Autowired
    public UserSeederDev(UserRepository userRepository, DatabaseStarting databaseStarting, VenueRepository venueRepository, @Value("${nightlife.password}") String PASSWORD, ReviewRepository reviewRepository, EventRepository eventRepository, AccessTypeRepository accessTypeRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.databaseStarting = databaseStarting;
        this.venueRepository = venueRepository;
        this.PASSWORD = PASSWORD;
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
        this.accessTypeRepository = accessTypeRepository;
        this.reservationRepository = reservationRepository;
        this.deleteAllAndInitializeAndSeedDataBase();

    }

    public void deleteAllAndInitializeAndSeedDataBase() {
        this.deleteAllAndInitialize();
        this.seedDataBase();
    }

    public void deleteAllAndInitialize() {
        this.reservationRepository.deleteAll();
        this.accessTypeRepository.deleteAll();
        this.eventRepository.deleteAll();
        this.reviewRepository.deleteAll();
        this.venueRepository.deleteAll();
        this.userRepository.deleteAll();
        log.warn("------- Deleted All -----------");
        this.databaseStarting.initialize();
    }

    private void seedDataBase() {
        log.warn("------- Initial Load from JAVA -----------");

        String pass = new BCryptPasswordEncoder().encode(PASSWORD);

        UserEntity[] users = {
                UserEntity.builder()
                        .email("newuser1@example.com")
                        .password(pass)
                        .firstName("NewUser1")
                        .lastName("LastName1")
                        .phone("1111111111")
                        .birthDate(LocalDate.of(1995, 2, 15))
                        .role(Role.ADMIN)
                        .build(),
                UserEntity.builder()
                        .email("newuser2@example.com")
                        .password(pass)
                        .firstName("NewUser2")
                        .lastName("LastName2")
                        .phone("2222222222")
                        .birthDate(LocalDate.of(1998, 5, 20))
                        .role(Role.ADMIN)
                        .build(),
                UserEntity.builder()
                        .email("newuser3@example.com")
                        .password(pass)
                        .firstName("NewUser3")
                        .lastName("LastName3")
                        .phone("3333333333")
                        .birthDate(LocalDate.of(2000, 7, 10))
                        .role(Role.OWNER)
                        .build(),
                UserEntity.builder()
                        .email("newuser4@example.com")
                        .password(pass)
                        .firstName("NewUser4")
                        .lastName("LastName4")
                        .phone("4444444444")
                        .birthDate(LocalDate.of(1992, 3, 5))
                        .role(Role.OWNER)
                        .build(),
                UserEntity.builder()
                        .email("newuser5@example.com")
                        .password(pass)
                        .firstName("NewUser5")
                        .lastName("LastName5")
                        .phone("5555555555")
                        .birthDate(LocalDate.of(1997, 9, 25))
                        .role(Role.CLIENT)
                        .build(),
                UserEntity.builder()
                        .email("newuser6@example.com")
                        .password(pass)
                        .firstName("NewUser6")
                        .lastName("LastName6")
                        .phone("6666666666")
                        .birthDate(LocalDate.of(1993, 12, 30))
                        .role(Role.CLIENT)
                        .build()
        };
        this.userRepository.saveAll(Arrays.asList(users));

        CoordinateEntity cuencaCoordinate = new CoordinateEntity();
        cuencaCoordinate.setLatitude(40.425242420453266);
        cuencaCoordinate.setLongitude(-3.7136387933938186);

        ProductEntity product = new ProductEntity();
        product.setName("Tinto de Verano");
        product.setPrice(3.0);

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setStartTime(LocalTime.of(23, 0));
        schedule.setEndTime(LocalTime.of(6, 0));
        schedule.setDayOfWeek(DayOfWeek.FRIDAY);

        VenueEntity venue1 = VenueEntity.builder()
                .name("Cuenca Club")
                .phone("1234567890")
                .LGTBFriendly(true)
                .instagram("cuencaclub_mad")
                .owner(users[2])
                .imageUrl("https://static.tumblr.com/5b25a0181dbd0b057fe53525233f5aa2/udkz9rq/B09n5vrlq/tumblr_static_2mxu4sctclussookgc8cg00cs_2048_v2.png")
                .coordinate(cuencaCoordinate)
                .musicGenres(Set.of(Music.POP))
                .products(List.of(
                        product
                ))
                .schedules(List.of(
                        schedule
                ))
                .build();

        CoordinateEntity kapitalCoordinate = new CoordinateEntity();
        kapitalCoordinate.setLatitude(40.40977335385098);
        kapitalCoordinate.setLongitude(-3.6931243933836155);

        VenueEntity venue2 = VenueEntity.builder()
                .name("Kapital")
                .phone("914202906")
                .LGTBFriendly(false)
                .instagram("teatrokapitaloficial")
                .imageUrl("https://discomadrid.com/wp-content/uploads/2015/08/Logo-Kapital-blanco.png")
                .owner(users[2])
                .coordinate(kapitalCoordinate)
                .musicGenres(Set.of(Music.POP, Music.HIP_HOP))
                .products(List.of(
                        product
                ))
                .schedules(List.of(
                        schedule
                ))
                .build();

        product.setVenue(venue1);
        schedule.setVenue(venue1);

        ProductEntity product2 = new ProductEntity();
        product2.setName("Tinto de Verano");
        product2.setPrice(3.0);
        product2.setVenue(venue2);

        ScheduleEntity schedule2 = new ScheduleEntity();
        schedule2.setStartTime(LocalTime.of(23, 0));
        schedule2.setEndTime(LocalTime.of(6, 0));
        schedule2.setDayOfWeek(DayOfWeek.FRIDAY);
        schedule2.setVenue(venue2);

        venue2.setProducts(List.of(product2));
        venue2.setSchedules(List.of(schedule2));

        this.venueRepository.saveAll(Arrays.asList(venue1, venue2));

        CoordinateEntity fitzCoordinate = new CoordinateEntity();
        fitzCoordinate.setLatitude(40.42469113003016);
        fitzCoordinate.setLongitude(-3.7124110491572577);

        ProductEntity fitzProduct = new ProductEntity();
        fitzProduct.setName("Vodka");
        fitzProduct.setPrice(12.0);

        ScheduleEntity fitzSchedule = new ScheduleEntity();
        fitzSchedule.setStartTime(LocalTime.of(23, 0));
        fitzSchedule.setEndTime(LocalTime.of(6, 0));
        fitzSchedule.setDayOfWeek(DayOfWeek.FRIDAY);

        VenueEntity venue3 = VenueEntity.builder()
                .name("Fitz")
                .phone("919930385")
                .LGTBFriendly(true)
                .instagram("fitzmadrid_")
                .owner(users[3])
                .imageUrl("https://discotecasmdz.com/wp-content/uploads/2023/11/Fitz-Club.png")
                .coordinate(fitzCoordinate)
                .musicGenres(Set.of(Music.POP, Music.ROCK))
                .products(List.of(fitzProduct))
                .schedules(List.of(fitzSchedule))
                .build();

        fitzProduct.setVenue(venue3);
        fitzSchedule.setVenue(venue3);

        CoordinateEntity copernicoCoordinate = new CoordinateEntity();
        copernicoCoordinate.setLatitude(40.435327111154784);
        copernicoCoordinate.setLongitude(-3.7136154754406094);

        ProductEntity copernicoProduct = new ProductEntity();
        copernicoProduct.setName("Cerveza");
        copernicoProduct.setPrice(3.0);

        ScheduleEntity copernicoSchedule = new ScheduleEntity();
        copernicoSchedule.setStartTime(LocalTime.of(23, 30));
        copernicoSchedule.setEndTime(LocalTime.of(6, 30));
        copernicoSchedule.setDayOfWeek(DayOfWeek.FRIDAY);

        VenueEntity venue4 = VenueEntity.builder()
                .name("Copernico")
                .phone("665530478")
                .LGTBFriendly(true)
                .instagram("copernicotheclub")
                .owner(users[3])
                .imageUrl("https://ugc.production.linktr.ee/9d6d718b-ef2b-4c14-a1c0-d58829946e6a_AVATAR-COPERNICO-4.jpeg?io=true&size=avatar-v3_0")
                .coordinate(copernicoCoordinate)
                .musicGenres(Set.of(Music.HIP_HOP))
                .products(List.of(copernicoProduct))
                .schedules(List.of(copernicoSchedule))
                .build();

        copernicoProduct.setVenue(venue4);
        copernicoSchedule.setVenue(venue4);

        CoordinateEntity fabrikCoordinate = new CoordinateEntity();
        fabrikCoordinate.setLatitude(40.26537782606181);
        fabrikCoordinate.setLongitude(-3.8405343798899096);

        ProductEntity fabrikProduct = new ProductEntity();
        fabrikProduct.setName("Vodka");
        fabrikProduct.setPrice(10.0);

        ScheduleEntity fabrikSchedule = new ScheduleEntity();
        fabrikSchedule.setStartTime(LocalTime.of(23, 0));
        fabrikSchedule.setEndTime(LocalTime.of(7, 0));
        fabrikSchedule.setDayOfWeek(DayOfWeek.SATURDAY);

        VenueEntity venue5 = VenueEntity.builder()
                .name("Fabrik")
                .phone("916156402")
                .LGTBFriendly(true)
                .instagram("fabrikmadrid")
                .owner(users[3])
                .imageUrl("https://djgoro.com/wp-content/uploads/2019/01/fabrik.jpg")
                .coordinate(fabrikCoordinate)
                .musicGenres(Set.of(Music.HIP_HOP))
                .products(List.of(fabrikProduct))
                .schedules(List.of(fabrikSchedule))
                .build();

        fabrikProduct.setVenue(venue5);
        fabrikSchedule.setVenue(venue5);

        this.venueRepository.saveAll(Arrays.asList(venue1, venue2, venue3, venue4, venue5));

        log.warn("        ------- users seeded");
    }
}

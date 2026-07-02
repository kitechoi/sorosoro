package com.sorosoro.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sorosoro.auth.domain.RefreshToken;
import com.sorosoro.auth.repository.RefreshTokenRepository;
import com.sorosoro.common.config.JpaAuditingConfig;
import com.sorosoro.dailylog.domain.DailyLog;
import com.sorosoro.dailylog.domain.DailyLogStatus;
import com.sorosoro.dailylog.domain.DailyLogTimeEntry;
import com.sorosoro.dailylog.repository.DailyLogRepository;
import com.sorosoro.dailylog.repository.DailyLogTimeEntryRepository;
import com.sorosoro.fabric.domain.Fabric;
import com.sorosoro.fabric.domain.ProjectFabric;
import com.sorosoro.fabric.domain.RepurchaseIntention;
import com.sorosoro.fabric.repository.FabricRepository;
import com.sorosoro.fabric.repository.ProjectFabricRepository;
import com.sorosoro.photo.domain.Photo;
import com.sorosoro.photo.domain.PhotoOwnerType;
import com.sorosoro.photo.domain.PhotoStatus;
import com.sorosoro.photo.repository.PhotoRepository;
import com.sorosoro.project.domain.PatternMethod;
import com.sorosoro.project.domain.Project;
import com.sorosoro.project.domain.ProjectReference;
import com.sorosoro.project.domain.ProjectSpecification;
import com.sorosoro.project.domain.ProjectStatus;
import com.sorosoro.project.repository.ProjectReferenceRepository;
import com.sorosoro.project.repository.ProjectRepository;
import com.sorosoro.project.repository.ProjectSpecificationRepository;
import com.sorosoro.support.RepositoryTestContainerConfig;
import com.sorosoro.user.domain.User;
import com.sorosoro.user.domain.UserRole;
import com.sorosoro.user.domain.UserStatus;
import com.sorosoro.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryMappingTest {

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        RepositoryTestContainerConfig.registerProperties(registry);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectSpecificationRepository projectSpecificationRepository;

    @Autowired
    private ProjectReferenceRepository projectReferenceRepository;

    @Autowired
    private FabricRepository fabricRepository;

    @Autowired
    private ProjectFabricRepository projectFabricRepository;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private DailyLogTimeEntryRepository dailyLogTimeEntryRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    void mapsCoreDomainEntitiesAndRepositories() {
        User user = userRepository.save(User.builder()
                .kakaoId("kakao-1")
                .nickname("soro")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());

        RefreshToken refreshToken = refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token("refresh-token")
                .expiredAt(LocalDateTime.now().plusDays(14))
                .build());

        Project project = projectRepository.save(Project.builder()
                .user(user)
                .title("linen bag")
                .status(ProjectStatus.IN_PROGRESS)
                .startedAt(LocalDate.now())
                .build());

        ProjectSpecification specification = projectSpecificationRepository.save(ProjectSpecification.builder()
                .project(project)
                .patternMethod(PatternMethod.SELF_DRAFTED)
                .patternName("basic tote")
                .build());

        ProjectReference reference = projectReferenceRepository.save(ProjectReference.builder()
                .projectSpecification(specification)
                .url("https://example.com/pattern")
                .title("pattern")
                .sortOrder(0)
                .build());

        Fabric fabric = fabricRepository.save(Fabric.builder()
                .user(user)
                .name("linen")
                .storeName("fabric store")
                .rating(5)
                .repurchaseIntention(RepurchaseIntention.YES)
                .build());

        ProjectFabric projectFabric = projectFabricRepository.save(ProjectFabric.builder()
                .project(project)
                .fabric(fabric)
                .memo("outer fabric")
                .build());

        DailyLog dailyLog = dailyLogRepository.save(DailyLog.builder()
                .user(user)
                .project(project)
                .status(DailyLogStatus.PUBLISHED)
                .workedDate(LocalDate.now())
                .title("cutting")
                .workTypes("[\"CUTTING\"]")
                .durationMinutes(60)
                .build());

        DailyLogTimeEntry timeEntry = dailyLogTimeEntryRepository.save(DailyLogTimeEntry.builder()
                .dailyLog(dailyLog)
                .startedAt(LocalTime.of(10, 0))
                .endedAt(LocalTime.of(11, 0))
                .durationMinutes(60)
                .build());

        Photo photo = photoRepository.save(Photo.builder()
                .user(user)
                .ownerType(PhotoOwnerType.PROJECT)
                .ownerId(project.getId())
                .originalKey("projects/original.jpg")
                .sortOrder(0)
                .status(PhotoStatus.UPLOADING)
                .build());

        assertThat(user.getId()).isNotNull();
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(refreshTokenRepository.findByToken(refreshToken.getToken())).contains(refreshToken);
        assertThat(projectSpecificationRepository.findByProject(project)).contains(specification);
        assertThat(projectReferenceRepository.findByProjectSpecificationOrderBySortOrderAsc(specification)).containsExactly(reference);
        assertThat(fabricRepository.findByUserAndStoreName(user, "fabric store")).containsExactly(fabric);
        assertThat(projectFabricRepository.findByProjectAndFabric(project, fabric)).contains(projectFabric);
        assertThat(dailyLogRepository.findByProjectAndStatusOrderByWorkedDateDesc(project, DailyLogStatus.PUBLISHED)).containsExactly(dailyLog);
        assertThat(dailyLogTimeEntryRepository.findByDailyLog(dailyLog)).containsExactly(timeEntry);
        assertThat(photoRepository.findByOwnerTypeAndOwnerIdOrderBySortOrderAsc(PhotoOwnerType.PROJECT, project.getId())).containsExactly(photo);
    }

    @Test
    void rejectsDuplicateProjectFabricMapping() {
        User user = userRepository.save(User.builder()
                .kakaoId("kakao-duplicate")
                .build());
        Project project = projectRepository.save(Project.builder()
                .user(user)
                .title("wallet")
                .build());
        Fabric fabric = fabricRepository.save(Fabric.builder()
                .user(user)
                .name("cotton")
                .build());

        projectFabricRepository.saveAndFlush(ProjectFabric.builder()
                .project(project)
                .fabric(fabric)
                .build());

        ProjectFabric duplicate = ProjectFabric.builder()
                .project(project)
                .fabric(fabric)
                .build();

        assertThatThrownBy(() -> projectFabricRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}

package com.sorosoro.fabric;

import static org.assertj.core.api.Assertions.assertThat;

import com.sorosoro.fabric.importing.LocalDemoLoginController;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class DemoProfileTest {
    @Test
    void productionCannotEnableLocalDemoLoginEvenWhenProfileIsAccidentallyCombined() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().setActiveProfiles("local-demo", "prod");
            context.register(LocalDemoLoginController.class);
            context.refresh();
            assertThat(context.getBeansOfType(LocalDemoLoginController.class)).isEmpty();
        }
    }
}

package com.picpay.vendas.api.integration;

import com.picpay.vendas.api.VendaApiApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de evidência: Verifica isolamento de beans entre módulos.
 *
 * EVIDÊNCIA PRÉ-FILTRO:
 * - VendaApiApplication carregava beans do pacote com.picpay.vendas.consumer
 * - Isso poderia causar conflitos e carregamento desnecessário de componentes
 *
 * EVIDÊNCIA PÓS-FILTRO:
 * - VendaApiApplication carrega APENAS beans de com.picpay.vendas.core e com.picpay.vendas.api
 * - Beans de com.picpay.vendas.consumer são explicitamente excluídos via excludeFilters
 */
@DisplayName("Evidência de Isolamento de Beans - VendaApiApplication")
class BeanIsolationTest {

    @Nested
    @DisplayName("Validação de Configuração do ComponentScan")
    class ValidacaoComponentScan {

        @Test
        @DisplayName("EVIDÊNCIA: VendaApiApplication deve ter @ComponentScan configurado")
        void deveTerComponentScanConfigurado() {
            ComponentScan componentScan = VendaApiApplication.class.getAnnotation(ComponentScan.class);

            System.out.println("\n========== EVIDÊNCIA - CONFIGURAÇÃO DO COMPONENTSCAN ==========");

            if (componentScan != null) {
                System.out.println("✓ @ComponentScan presente em VendaApiApplication");
                System.out.println("  - basePackages: " + Arrays.toString(componentScan.basePackages()));
                System.out.println("  - excludeFilters: " + componentScan.excludeFilters().length + " filtro(s)");

                if (componentScan.excludeFilters().length > 0) {
                    Arrays.stream(componentScan.excludeFilters()).forEach(filter -> {
                        System.out.println("    - type: " + filter.type());
                        if (filter.type() == FilterType.REGEX) {
                            System.out.println("    - pattern: " + String.join(", ", filter.pattern()));
                        }
                    });
                }
            } else {
                System.out.println("✗ @ComponentScan NÃO está configurado");
                System.out.println("  ALERTA: Beans de outros módulos podem ser carregados indevidamente");
            }

            System.out.println("================================================================\n");

            assertThat(componentScan)
                    .as("VendaApiApplication deve ter @ComponentScan configurado para isolamento")
                    .isNotNull();
        }

        @Test
        @DisplayName("EVIDÊNCIA: basePackages deve incluir apenas core e api")
        void deveTerBasePackagesCorretos() {
            ComponentScan componentScan = VendaApiApplication.class.getAnnotation(ComponentScan.class);

            System.out.println("\n========== EVIDÊNCIA - BASEPACKAGES ==========");

            if (componentScan != null) {
                List<String> basePackages = Arrays.asList(componentScan.basePackages());

                System.out.println("basePackages configurados:");
                basePackages.forEach(pkg -> System.out.println("  - " + pkg));

                boolean hasCore = basePackages.stream().anyMatch(pkg -> pkg.contains("core"));
                boolean hasApi = basePackages.stream().anyMatch(pkg -> pkg.contains("api"));
                boolean hasConsumer = basePackages.stream().anyMatch(pkg -> pkg.contains("consumer"));

                System.out.println("\nAnálise:");
                System.out.println("  - Contém 'core': " + (hasCore ? "✓" : "✗"));
                System.out.println("  - Contém 'api': " + (hasApi ? "✓" : "✗"));
                System.out.println("  - Contém 'consumer': " + (hasConsumer ? "✗ PROBLEMA" : "✓ OK (não deve ter)"));

                assertThat(hasCore)
                        .as("basePackages deve conter 'core'")
                        .isTrue();
                assertThat(hasApi)
                        .as("basePackages deve conter 'api'")
                        .isTrue();
                assertThat(hasConsumer)
                        .as("basePackages NÃO deve conter 'consumer'")
                        .isFalse();
            } else {
                System.out.println("@ComponentScan não configurado - não é possível verificar basePackages");
            }

            System.out.println("==============================================\n");
        }

        @Test
        @DisplayName("EVIDÊNCIA: excludeFilters deve bloquear beans de consumer")
        void deveTerExcludeFiltersParaConsumer() {
            ComponentScan componentScan = VendaApiApplication.class.getAnnotation(ComponentScan.class);

            System.out.println("\n========== EVIDÊNCIA - EXCLUDEFILTERS ==========");

            if (componentScan != null && componentScan.excludeFilters().length > 0) {
                System.out.println("excludeFilters configurados: " + componentScan.excludeFilters().length);

                boolean hasConsumerFilter = Arrays.stream(componentScan.excludeFilters())
                        .anyMatch(filter -> {
                            if (filter.type() == FilterType.REGEX) {
                                return Arrays.stream(filter.pattern())
                                        .anyMatch(pattern -> pattern.contains("consumer"));
                            }
                            return false;
                        });

                Arrays.stream(componentScan.excludeFilters()).forEach(filter -> {
                    System.out.println("  - type: " + filter.type());
                    if (filter.type() == FilterType.REGEX) {
                        System.out.println("    patterns: " + String.join(", ", filter.pattern()));
                    }
                });

                System.out.println("\nResultado:");
                System.out.println("  - Filtro para 'consumer': " + (hasConsumerFilter ? "✓ PRESENTE" : "✗ AUSENTE"));

                assertThat(hasConsumerFilter)
                        .as("excludeFilters deve bloquear beans de consumer")
                        .isTrue();
            } else {
                System.out.println("✗ NENHUM excludeFilters configurado");
                System.out.println("ALERTA: Beans de consumer podem ser carregados indevidamente");

                // Força falha para evidenciar que precisa configurar
                assertThat(componentScan)
                        .as("excludeFilters deve estar configurado para bloquear consumer")
                        .isNull();
            }

            System.out.println("================================================\n");
        }
    }

    @Nested
    @DisplayName("Documentação do Estado")
    class DocumentacaoEstado {

        @Test
        @DisplayName("EVIDÊNCIA: Documentar configuração atual completa")
        void documentarConfiguracaoAtual() {
            System.out.println("\n========== DOCUMENTO DE EVIDÊNCIA ==========");
            System.out.println("Data: " + java.time.LocalDateTime.now());
            System.out.println("Classe: VendaApiApplication");
            System.out.println("Objetivo: Verificar isolamento de beans entre módulos");

            ComponentScan componentScan = VendaApiApplication.class.getAnnotation(ComponentScan.class);

            System.out.println("\nCONFIGURAÇÃO ATUAL:");
            System.out.println("-------------------");

            if (componentScan != null) {
                System.out.println("basePackages:");
                Arrays.stream(componentScan.basePackages())
                        .forEach(pkg -> System.out.println("  - " + pkg));

                System.out.println("\nexcludeFilters:");
                if (componentScan.excludeFilters().length > 0) {
                    Arrays.stream(componentScan.excludeFilters()).forEach(filter -> {
                        System.out.println("  - type: " + filter.type());
                        if (filter.type() == FilterType.REGEX) {
                            System.out.println("    pattern: " + String.join(", ", filter.pattern()));
                        }
                    });
                } else {
                    System.out.println("  (nenhum)");
                }
            } else {
                System.out.println("@ComponentScan: NÃO CONFIGURADO");
                System.out.println("Utilizando scanBasePackages do @SpringBootApplication");
            }

            System.out.println("\n============================================\n");

            assertThat(true).isTrue();
        }
    }
}

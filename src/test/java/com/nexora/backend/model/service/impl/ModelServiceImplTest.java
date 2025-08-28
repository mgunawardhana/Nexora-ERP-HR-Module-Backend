package com.nexora.backend.model.service.impl;

        import com.nexora.backend.authentication.repository.EmployeeDetailsRepository;
        import com.nexora.backend.domain.entity.EmployeeDetails;
        import com.nexora.backend.domain.response.APIResponse;
        import com.nexora.backend.domain.response.dto.PredictionResponse;
        import com.nexora.backend.util.ResponseUtil;
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.DisplayName;
        import org.junit.jupiter.api.Test;
        import org.junit.jupiter.api.extension.ExtendWith;
        import org.mockito.InjectMocks;
        import org.mockito.Mock;
        import org.mockito.junit.jupiter.MockitoExtension;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.reactive.function.client.WebClient;
        import reactor.core.publisher.Mono;

        import java.util.Optional;

        import static org.assertj.core.api.Assertions.assertThat;
        import static org.mockito.ArgumentMatchers.any;
        import static org.mockito.ArgumentMatchers.anyString;
        import static org.mockito.Mockito.when;

        @ExtendWith(MockitoExtension.class)
        @DisplayName("ModelServiceImpl Tests")
        class ModelServiceImplTest {

            @Mock
            private EmployeeDetailsRepository employeeDetailsRepository;
            @Mock
            private WebClient webClient;
            @Mock
            private ResponseUtil responseUtil;
            @Mock
            private WebClient.RequestBodyUriSpec requestBodyUriSpec;
            @Mock
            private WebClient.RequestBodySpec requestBodySpec;
            @Mock
            private WebClient.RequestHeadersSpec requestHeadersSpec;
            @Mock
            private WebClient.ResponseSpec responseSpec;

            @InjectMocks
            private ModelServiceImpl modelService;

            private EmployeeDetails employeeDetails;

            @BeforeEach
            void setUp() {
                employeeDetails = EmployeeDetails.builder().id(1L).employeeName("Test User").build();
            }

            @Test
            @DisplayName("Success: Get Prediction for Employee")
            void getPredictionForEmployee_Success() {
                PredictionResponse prediction = new PredictionResponse();
                when(employeeDetailsRepository.findByUserId(1)).thenReturn(Optional.of(employeeDetails));
                when(webClient.post()).thenReturn(requestBodyUriSpec);
                when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
                when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
                when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
                when(responseSpec.bodyToMono(PredictionResponse.class)).thenReturn(Mono.just(prediction));
                when(responseUtil.wrapSuccess(any(), any(HttpStatus.class)))
                        .thenReturn(ResponseEntity.ok().build());
                ResponseEntity<APIResponse> response = modelService.getPredictionForEmployee(1);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            }

            @Test
            @DisplayName("Error: Employee Not Found for Prediction")
            void getPredictionForEmployee_EmployeeNotFound() {
                when(employeeDetailsRepository.findByUserId(1)).thenReturn(Optional.empty());
                when(responseUtil.wrapError(anyString(), anyString(), any(HttpStatus.class)))
                        .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                ResponseEntity<APIResponse> response = modelService.getPredictionForEmployee(1);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            }

            @Test
            @DisplayName("Error: Prediction Service Fails")
            void getPredictionForEmployee_PredictionServiceError() {
                when(employeeDetailsRepository.findByUserId(1)).thenReturn(Optional.of(employeeDetails));
                when(webClient.post()).thenThrow(new RuntimeException("Connection refused"));
                when(responseUtil.wrapError(anyString(), anyString(), any(HttpStatus.class)))
                        .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                ResponseEntity<APIResponse> response = modelService.getPredictionForEmployee(1);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
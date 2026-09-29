package com.pragma.plazoletaservice.application.usecase;

import com.pragma.plazoletaservice.application.constants.ApplicationConstants;
import com.pragma.plazoletaservice.domain.model.Category;
import com.pragma.plazoletaservice.domain.spi.ICategoryPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryUseCaseTest {

    @Mock
    private ICategoryPersistencePort categoryPersistencePort;

    @InjectMocks
    private CategoryUseCase categoryUseCase;

    @Test
    @DisplayName("guarda la categoría cuando el nombre es válido y no existe")
    void savesNewCategory() {
        // given
        when(categoryPersistencePort.existsCategoryByName("Postres")).thenReturn(false);

        // when
        categoryUseCase.createCategory("Postres");

        // then
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryPersistencePort).saveCategory(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(captor.getValue().getName()).isEqualTo("Postres");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("rechaza nombres vacíos sin consultar ni guardar")
    void rejectsBlankName(String name) {
        // when / then
        assertThatThrownBy(() -> categoryUseCase.createCategory(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ApplicationConstants.MSG_CATEGORY_NAME_REQUIRED);
        verify(categoryPersistencePort, never()).existsCategoryByName(anyString());
        verify(categoryPersistencePort, never()).saveCategory(any());
    }

    @Test
    @DisplayName("rechaza una categoría que ya existe")
    void rejectsDuplicatedCategory() {
        // given
        when(categoryPersistencePort.existsCategoryByName("Postres")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> categoryUseCase.createCategory("Postres"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ApplicationConstants.MSG_CATEGORY_ALREADY_EXISTS);
        verify(categoryPersistencePort, never()).saveCategory(any());
    }
}

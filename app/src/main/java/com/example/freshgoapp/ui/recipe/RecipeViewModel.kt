    package com.example.freshgoapp.ui.recipe

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.freshgoapp.domain.model.Recipe
    import com.example.freshgoapp.domain.model.RecipeDetail // <-- IMPORT PENTING INI
    import com.example.freshgoapp.domain.usecase.GetRecipeDetailUseCase
    import com.example.freshgoapp.domain.usecase.GetRecipesUseCase
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.launch

    sealed interface RecipeUiState {
        object Loading : RecipeUiState
        data class Success(val recipes: List<Recipe>) : RecipeUiState
        data class Error(val errorMessage: String) : RecipeUiState
    }

    sealed interface RecipeDetailUiState {
        object Loading : RecipeDetailUiState
        // DI SINI KUNCINYA: Harus menggunakan RecipeDetail, bukan Recipe biasa
        data class Success(val recipeDetail: RecipeDetail) : RecipeDetailUiState
        data class Error(val errorMessage: String) : RecipeDetailUiState
    }

    class RecipeViewModel : ViewModel() {
        private val getRecipesUseCase = GetRecipesUseCase()
        private val getRecipeDetailUseCase = GetRecipeDetailUseCase()

        private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
        val uiState: StateFlow<RecipeUiState> = _uiState

        private val _detailUiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
        val detailUiState: StateFlow<RecipeDetailUiState> = _detailUiState

        fun fetchRecipes(ingredient: String) {
            viewModelScope.launch {
                _uiState.value = RecipeUiState.Loading
                try {
                    val list = getRecipesUseCase.execute(ingredient)
                    if (list.isEmpty()) {
                        _uiState.value = RecipeUiState.Error("Tidak ada resep masakan yang cocok untuk bahan ini.")
                    } else {
                        _uiState.value = RecipeUiState.Success(list)
                    }
                } catch (e: Exception) {
                    _uiState.value = RecipeUiState.Error("Gagal memuat data. Periksa jaringan internet Anda.")
                }
            }
        }

        fun fetchRecipeDetail(recipeId: String) {
            viewModelScope.launch {
                _detailUiState.value = RecipeDetailUiState.Loading
                try {
                    val detail = getRecipeDetailUseCase.execute(recipeId)
                    if (detail != null) {
                        _detailUiState.value = RecipeDetailUiState.Success(detail)
                    } else {
                        _detailUiState.value = RecipeDetailUiState.Error("Resep tidak ditemukan.")
                    }
                } catch (e: Exception) {
                    _detailUiState.value = RecipeDetailUiState.Error("Gagal memuat detail resep.")
                }
            }
        }
    }
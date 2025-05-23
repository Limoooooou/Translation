import com.example.translation.api.service.TextTranslationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideTextTranslationService(retrofit: Retrofit): TextTranslationService {
        return retrofit.create(TextTranslationService::class.java)
    }
}
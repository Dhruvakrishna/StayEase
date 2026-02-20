package com.example.stayease.di

import android.content.Context
import androidx.room.Room
import com.example.stayease.BuildConfig
import com.example.stayease.core.telemetry.NoopTelemetry
import com.example.stayease.core.telemetry.Telemetry
import com.example.stayease.data.auth.*
import com.example.stayease.data.local.AppDatabase
import com.example.stayease.data.remote.api.BookingApi
import com.example.stayease.data.remote.api.OverpassApi
import com.example.stayease.data.repository.*
import com.example.stayease.domain.repository.*
import com.example.stayease.telemetry.FirebaseTelemetry
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OverpassRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BookingRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PublicOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProtectedOkHttp

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides @Singleton fun tokenStore(@ApplicationContext ctx: Context) = TokenStore(ctx)
  @Provides @Singleton fun authProvider(): AuthProvider = DemoAuthProvider()
  @Provides @Singleton fun authRepository(store: TokenStore, provider: AuthProvider): AuthRepository = AuthRepositoryImpl(store, provider)
  @Provides @Singleton fun sessionRepository(store: TokenStore): SessionRepository = SessionRepositoryImpl(store)

  @Provides @Singleton fun moshi(): Moshi = Moshi.Builder().build()

  @PublicOkHttp
  @Provides @Singleton
  fun publicOkHttp(): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    return OkHttpClient.Builder()
      .addInterceptor(logging)
      .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
      .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
      .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
      .connectionPool(okhttp3.ConnectionPool(8, 5, java.util.concurrent.TimeUnit.MINUTES))
      .build()
  }

  @ProtectedOkHttp
  @Provides @Singleton
  fun protectedOkHttp(authInterceptor: AuthInterceptor): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    return OkHttpClient.Builder()
      .addInterceptor(authInterceptor)
      .addInterceptor(logging)
      .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
      .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
      .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
      .connectionPool(okhttp3.ConnectionPool(8, 5, java.util.concurrent.TimeUnit.MINUTES))
      .build()
  }

  @OverpassRetrofit
  @Provides @Singleton
  fun overpassRetrofit(@PublicOkHttp client: OkHttpClient, moshi: Moshi): Retrofit {
    // Optimization: Add Gzip and better connection management is already handled by OkHttp.
    // We'll ensure the client is optimized.
    return Retrofit.Builder()
      .baseUrl(BuildConfig.OVERPASS_BASE_URL)
      .client(client)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
  }

  @Provides @Singleton fun overpassApi(@OverpassRetrofit retrofit: Retrofit): OverpassApi = retrofit.create(OverpassApi::class.java)

  @BookingRetrofit
  @Provides @Singleton
  fun bookingRetrofit(@ProtectedOkHttp client: OkHttpClient, moshi: Moshi): Retrofit =
    Retrofit.Builder()
      .baseUrl(BuildConfig.BOOKING_BASE_URL)
      .client(client)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

  @Provides @Singleton fun bookingApi(@BookingRetrofit retrofit: Retrofit): BookingApi = retrofit.create(BookingApi::class.java)

  @Provides @Singleton
  fun db(@ApplicationContext ctx: Context): AppDatabase =
    Room.databaseBuilder(ctx, AppDatabase::class.java, "stayease.db")
      .fallbackToDestructiveMigration()
      .setJournalMode(androidx.room.RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING) // Performance: WAL mode
      .build()

  @Provides @Singleton fun stayRepository(api: OverpassApi, db: AppDatabase): StayRepository = StayRepositoryImpl(api, db)
  @Provides @Singleton fun bookingRemote(api: BookingApi): BookingRemoteDataSource = BookingRemoteDataSourceImpl(api)
  @Provides @Singleton fun bookingRepository(db: AppDatabase, remote: BookingRemoteDataSource, telemetry: Telemetry): BookingRepository =
    BookingRepositoryImpl(db.bookingDao(), remote, telemetry)

  @Provides @Singleton fun userRepository(db: AppDatabase): UserRepository = UserRepositoryImpl(db.userDao())
  @Provides @Singleton fun cmsRepository(): CmsRepository = CmsRepositoryImpl()
  @Provides @Singleton fun settingsRepository(@ApplicationContext ctx: Context): SettingsRepository = SettingsRepositoryImpl(ctx)

  @Provides @Singleton fun locationProvider(@ApplicationContext ctx: Context): LocationProvider = LocationProviderImpl(ctx)

  @Provides @Singleton
  fun telemetry(@ApplicationContext ctx: Context): Telemetry {
    return if (BuildConfig.FIREBASE_ENABLED) {
      val analytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(ctx)
      val crash = com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance()
      FirebaseTelemetry(analytics, crash)
    } else {
      NoopTelemetry()
    }
  }
}

package com.example.stayease.feature.asyncdemo;

import com.example.stayease.data.remote.api.OverpassApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class AsyncDemoViewModel_Factory implements Factory<AsyncDemoViewModel> {
  private final Provider<OverpassApi> apiProvider;

  public AsyncDemoViewModel_Factory(Provider<OverpassApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public AsyncDemoViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static AsyncDemoViewModel_Factory create(Provider<OverpassApi> apiProvider) {
    return new AsyncDemoViewModel_Factory(apiProvider);
  }

  public static AsyncDemoViewModel newInstance(OverpassApi api) {
    return new AsyncDemoViewModel(api);
  }
}

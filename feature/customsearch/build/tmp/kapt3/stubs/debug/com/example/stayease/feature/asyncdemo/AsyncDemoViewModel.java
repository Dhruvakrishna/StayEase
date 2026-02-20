package com.example.stayease.feature.asyncdemo;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000fJ\u0006\u0010\u0011\u001a\u00020\u000fJ\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0082@\u00a2\u0006\u0002\u0010\u0016R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u0017"}, d2 = {"Lcom/example/stayease/feature/asyncdemo/AsyncDemoViewModel;", "Landroidx/lifecycle/ViewModel;", "api", "Lcom/example/stayease/data/remote/api/OverpassApi;", "(Lcom/example/stayease/data/remote/api/OverpassApi;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/stayease/feature/asyncdemo/AsyncUiState;", "job", "Lkotlinx/coroutines/Job;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "cancel", "", "runParallelQueries", "runRetryBackoff", "tryCall", "", "attempt", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "customsearch_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class AsyncDemoViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.stayease.data.remote.api.OverpassApi api = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.stayease.feature.asyncdemo.AsyncUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.example.stayease.feature.asyncdemo.AsyncUiState> state = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job job;
    
    @javax.inject.Inject()
    public AsyncDemoViewModel(@org.jetbrains.annotations.NotNull()
    com.example.stayease.data.remote.api.OverpassApi api) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.example.stayease.feature.asyncdemo.AsyncUiState> getState() {
        return null;
    }
    
    public final void cancel() {
    }
    
    public final void runParallelQueries() {
    }
    
    public final void runRetryBackoff() {
    }
    
    private final java.lang.Object tryCall(int attempt, kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
}
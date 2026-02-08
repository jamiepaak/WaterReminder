package com.example.myapplication.domain.usecase.base

import com.example.myapplication.domain.util.Resource
import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<in P, R> {
    operator fun invoke(params: P): Flow<Resource<R>> = execute(params)
    protected abstract fun execute(params: P): Flow<Resource<R>>
}

abstract class NoParamFlowUseCase<R> {
    operator fun invoke(): Flow<Resource<R>> = execute()
    protected abstract fun execute(): Flow<Resource<R>>
}

abstract class SuspendUseCase<in P, R> {
    suspend operator fun invoke(params: P): Resource<R> = execute(params)
    protected abstract suspend fun execute(params: P): Resource<R>
}

abstract class NoParamSuspendUseCase<R> {
    suspend operator fun invoke(): Resource<R> = execute()
    protected abstract suspend fun execute(): Resource<R>
}

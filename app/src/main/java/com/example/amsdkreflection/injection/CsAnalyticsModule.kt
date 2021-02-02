package com.example.amsdkreflection.injection

import android.content.Context
import com.example.amsdkreflection.sdk.CsAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CsAnalyticsModule {

    @Singleton
    @Provides
    fun provideCsAnalytics(@ApplicationContext appContext: Context): CsAnalytics {
        return CsAnalytics(appContext)
    }
}
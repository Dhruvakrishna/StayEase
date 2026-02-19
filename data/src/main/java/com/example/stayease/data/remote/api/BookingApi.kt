package com.example.stayease.data.remote.api
import com.example.stayease.data.remote.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingApi {
  @POST("v1/bookings")
  suspend fun createBooking(@Body body: CreateBookingRequestDto): CreateBookingResponseDto

  @GET("v1/bookings")
  suspend fun listBookings(): BookingListResponseDto

  @POST("v1/bookings/{bookingId}/cancel")
  suspend fun cancel(@Path("bookingId") bookingId: String): CancelBookingResponseDto
}

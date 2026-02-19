package com.example.stayease.data

import com.example.stayease.data.remote.api.BookingApi
import com.example.stayease.data.remote.dto.CreateBookingRequestDto
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class BookingApiTest {
  private lateinit var server: MockWebServer
  private lateinit var api: BookingApi

  @Before fun setup() {
    server = MockWebServer()
    server.start()
    api = Retrofit.Builder()
      .baseUrl(server.url("/"))
      .client(OkHttpClient.Builder().build())
      .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
      .build()
      .create(BookingApi::class.java)
  }

  @After fun tearDown() { server.shutdown() }

  @Test fun create_booking_parses_response() = kotlinx.coroutines.runBlocking {
    server.enqueue(MockResponse().setResponseCode(201).setBody(
      """{"bookingId":"b1","confirmationCode":"SE-123456","status":"CONFIRMED","totalAmount":200.0,"currency":"USD","createdAtEpochMs":1700000000000}"""
    ))
    val res = api.createBooking(CreateBookingRequestDto(
      stayId = 1, stayName = "Stay", checkInEpochDay = 10, checkOutEpochDay = 12,
      guests = 2, rooms = 1, roomType = "Standard", specialRequests = null, currency = "USD"
    ))
    assertEquals("b1", res.bookingId)
  }
}

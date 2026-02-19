package com.example.stayease.data.repository
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.stayease.data.remote.api.OverpassApi
import com.example.stayease.data.remote.mapper.toStay
import com.example.stayease.data.remote.query.staysAround
import com.example.stayease.domain.model.GeoPoint
import com.example.stayease.domain.model.Stay

class StaysPagingSource(
  private val api: OverpassApi,
  private val pivot: GeoPoint,
  private val radiusMeters: Int
) : PagingSource<Int, Stay>() {

  override fun getRefreshKey(state: PagingState<Int, Stay>): Int? =
    state.anchorPosition?.let { pos -> state.closestPageToPosition(pos)?.prevKey?.plus(1) ?: 0 }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stay> = try {
    val page = params.key ?: 0
    val res = api.query(staysAround(pivot, radiusMeters))
    val all = res.elements.mapNotNull { it.toStay() }.distinctBy { it.id }.sortedBy { it.nightlyPriceUsdEstimate }
    val pageSize = params.loadSize.coerceAtMost(25)
    val from = page * pageSize
    val to = (from + pageSize).coerceAtMost(all.size)
    val slice = if (from >= all.size) emptyList() else all.subList(from, to)
    val next = if (to >= all.size) null else page + 1
    val prev = if (page == 0) null else page - 1
    LoadResult.Page(slice, prevKey = prev, nextKey = next)
  } catch (t: Throwable) { LoadResult.Error(t) }
}

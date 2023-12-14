package com.capstone.mangbeli.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.capstone.mangbeli.data.local.entity.RemoteKeys
import com.capstone.mangbeli.data.local.entity.VendorEntity
import com.capstone.mangbeli.data.local.room.VendorDatabase
import com.capstone.mangbeli.data.remote.network.ApiService
import com.capstone.mangbeli.utils.toVendorEntity

@OptIn(ExperimentalPagingApi::class)
class VendorRemoteMediator(
    private val apiService: ApiService,
    private val database: VendorDatabase,
    private val location: Int = 1,
    private val isLocationNotEnable: Int = 1,
    private val search: String? = "",
    private val filter: String? = ""
) : RemoteMediator<Int, VendorEntity>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, VendorEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
        }

        try {
            val response = apiService.getVendors(page, state.config.pageSize, location, isLocationNotEnable, search, filter)

            val vendors = response.listVendors
            val endOfPage = vendors.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().deleteRemoteKeys()
                    database.vendorDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPage) null else page + 1

                val remoteKeys = vendors.map {
                    RemoteKeys(id = it.vendorId ?: "", prevKey = prevKey, nextKey = nextKey)
                }

                val vendorEntities= vendors.map { vendor ->
                    vendor.toVendorEntity()
                }

                database.remoteKeysDao().insertAll(remoteKeys)
                database.vendorDao().insertVendor(vendorEntities)
            }

            return MediatorResult.Success(endOfPage)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, VendorEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.vendorId ?: "")
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, VendorEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.vendorId ?: "")
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, VendorEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.vendorId?.let { vendorId ->
                database.remoteKeysDao().getRemoteKeysId(vendorId)
            }
        }
    }
}

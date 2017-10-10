/**
 * NASATv
 *
 * Created by iomusashi on 9/28/17.
 * Copyright (c) 2017 iomusashi. All rights reserved.
 */

package co.blackfintech.nasatv.apiclient

import co.blackfintech.nasatv.apiclient.entities.ChannelResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

////////////////////////////////////////////////////////////////////////////////////////////////////
interface ChannelService {

  @GET("channels/{channelId}.json")
  fun getChannels(@Path("channelId") channelId: String) : Call<ChannelResponse>
}
////////////////////////////////////////////////////////////////////////////////////////////////////

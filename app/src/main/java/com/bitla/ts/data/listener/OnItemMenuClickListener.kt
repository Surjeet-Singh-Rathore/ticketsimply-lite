package com.bitla.ts.data.listener

import com.bitla.ts.domain.pojo.alloted_services.Service
import java.util.ArrayList


interface OnItemMenuClickListener {
    fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: com.bitla.ts.domain.pojo.allotedServiceDirect.AllotedDirctResponse.Service)
}



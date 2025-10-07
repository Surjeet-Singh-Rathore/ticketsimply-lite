package com.bitla.ts.app.base

import android.content.*
import android.os.*
import android.view.*
import android.view.inputmethod.*
import androidx.fragment.app.*
import com.bitla.ts.R
import com.bitla.ts.data.listener.*
import com.bitla.ts.domain.pojo.available_routes.Result
import com.bitla.ts.domain.pojo.destination_pair.*
import com.bitla.ts.presentation.adapter.*
import com.bitla.ts.presentation.view.activity.DomainActivity
import com.bitla.ts.utils.common.clearAndSave
import com.bitla.ts.utils.dialog.DialogUtils
import com.bitla.ts.utils.sharedPref.PREF_IS_USER_LOGIN
import com.bitla.ts.utils.sharedPref.PREF_LOGGED_IN_USER
import com.bitla.ts.utils.sharedPref.PREF_TRAVEL_DATE
import com.bitla.ts.utils.sharedPref.PreferenceUtils

abstract class BaseFragment : Fragment(), DialogButtonListener, OnItemClickListener, DialogSingleButtonListener {

    private var searchList1 = mutableListOf<SearchModel>()
    private lateinit var editPassengersAdapter: EditPassengersAdapter
    private lateinit var networkConnection: BaseNetworkConnectionObserver


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkConnection = BaseNetworkConnectionObserver(requireActivity())
        networkConnection.observe(requireActivity()) { isConnected ->
            if (isConnected != null) {
                if (isConnected) {
                    isInternetOnCallApisAndInitUI()
                }else{
                    isNetworkOff()
                }
            }
        }
    }
    abstract fun isInternetOnCallApisAndInitUI()
    abstract fun isNetworkOff()

    override fun onLeftButtonClick() {

    }

    override fun onRightButtonClick() {

    }

    override fun onClickOfNavMenu(position: Int) {

    }

    override fun onClick(view: View, position: Int) {

    }

    override fun onClickOfItem(data: String, position: Int) {

    }

    override fun onMenuItemClick(itemPosition: Int, menuPosition: Int, busData: Result) {

    }

    override fun onSingleButtonClick(str: String) {

    }

    fun showUnauthorisedDialog() {
        DialogUtils.unAuthorizedDialog(requireActivity(),
            "${getString(R.string.authentication_failed)}\n\n ${getString(R.string.please_try_again)}",
            object : DialogSingleButtonListener {
                override fun onSingleButtonClick(str: String) {
                    openDomainActivity()
                }
            }
        )
    }

    private fun openDomainActivity() {
        PreferenceUtils.putObject(null, PREF_LOGGED_IN_USER)
        PreferenceUtils.removeKey(PREF_TRAVEL_DATE)
        clearAndSave(requireActivity())
        PreferenceUtils.putString(PREF_IS_USER_LOGIN, "false")
        val intent = Intent(requireActivity(), DomainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }



    fun closeKeyBoard() {
        val view = activity?.currentFocus
        if (view != null) {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun isNumeric(string: String): Boolean {
        return string.matches("^[0-9]*$".toRegex())
    }
}
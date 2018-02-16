package us.echols.embyplaylistmaker.ui.common

import android.app.Fragment
import android.os.Bundle
import butterknife.ButterKnife
import butterknife.Unbinder

abstract class BaseFragment : Fragment() {

    private lateinit var viewUnbinder: Unbinder

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        ButterKnife.setDebug(false)

        if (view != null) {
            viewUnbinder = ButterKnife.bind(this, view)
        }
    }

    override fun onDestroyView() {
        viewUnbinder.unbind()
        super.onDestroyView()
    }

}

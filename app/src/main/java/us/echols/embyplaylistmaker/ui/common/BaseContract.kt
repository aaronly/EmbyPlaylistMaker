package us.echols.embyplaylistmaker.ui.common

interface BaseContract {

    interface View<out P> {
        val presenter: P

    }

    interface Presenter<V : View<*>> {

        var view: V?

        fun attachView(view: V) {
            this.view = view
        }

        fun detachView() {
            view = null
        }
    }
}
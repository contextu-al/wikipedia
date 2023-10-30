package org.wikipedia.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.contextu.al.BuildConfig
import com.contextu.al.Contextual
import com.contextu.al.Contextual.setUserId
import com.contextu.al.Contextual.tagString
import com.contextu.al.Contextual.tagStringArray
import com.contextu.al.CtxUiObserver
import com.contextu.al.core.CtxEventObserver
import com.contextu.al.debug.Log
import com.contextu.al.model.GuidePayload
import org.wikipedia.Constants
import org.wikipedia.R
import org.wikipedia.WikipediaApp
import org.wikipedia.activity.SingleFragmentActivity
import org.wikipedia.appshortcuts.AppShortcuts.Companion.setShortcuts
import org.wikipedia.databinding.ActivityMainBinding
import org.wikipedia.navtab.NavTab
import org.wikipedia.page.PageActivity
import org.wikipedia.page.tabs.TabActivity
import org.wikipedia.settings.Prefs
import org.wikipedia.suggestededits.SuggestedEditsTasksFragment
import org.wikipedia.util.DimenUtil
import org.wikipedia.util.FeedbackUtil
import org.wikipedia.util.ResourceUtil
import org.wikipedia.views.TabCountsView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : SingleFragmentActivity<MainFragment>(), MainFragment.Callback {
    private lateinit var binding: ActivityMainBinding

    private var tabCountsView: TabCountsView? = null
    private var controlNavTabInFragment = false
    private var showTabCountsAnimation = false

    private val guideJson = "{\n" +
        "    \"buttons\":{\n" +
        "        \"dismiss\":{\n" +
        "           \"color\":\"#000000\",\n" +
        "           \"height\":16,\n" +
        "           \"width\":16\n" +
        "        },\n" +
        "        \"layout\":\"horizontal\",\n" +
        "        \"next\":{\n" +
        "           \"align\":\"left\",\n" +
        "           \"border-color\":\"#2E7D32\",\n" +
        "           \"border-width\":1,\n" +
        "           \"button-align\":\"right\",\n" +
        "           \"color\":\"#000000\",\n" +
        "           \"corner-radius\":5,\n" +
        "           \"font-size\":14,\n" +
        "           \"margin-right\":10,\n" +
        "           \"padding-left\":10,\n" +
        "           \"padding-right\":10,\n" +
        "           \"padding-top\":0,\n" +
        "           \"placement\":\"right\",\n" +
        "           \"text-align\":\"center\",\n" +
        "           \"type\":\"button\"\n" +
        "        },\n" +
        "        \"prev\":{\n" +
        "           \"align\":\"left\",\n" +
        "           \"border-color\":\"#2E7D32\",\n" +
        "           \"border-width\":1,\n" +
        "           \"button-align\":\"left\",\n" +
        "           \"corner-radius\":5,\n" +
        "           \"margin-left\":10,\n" +
        "           \"padding-left\":10,\n" +
        "           \"padding-right\":10,\n" +
        "           \"placement\":\"left\",\n" +
        "           \"text-align\":\"center\",\n" +
        "           \"type\":\"button\"\n" +
        "        }\n" +
        "     },\n" +
        "     \"content\":{\n" +
        "        \"background-color\":\"#00000000\",\n" +
        "        \"color\":\"#000000\",\n" +
        "        \"font-size\":\"-1\",\n" +
        "        \"line-height\":\"120%\",\n" +
        "        \"margin-bottom\":10,\n" +
        "        \"margin-left\":10,\n" +
        "        \"margin-right\":10,\n" +
        "        \"margin-top\":10,\n" +
        "        \"padding-bottom\":10,\n" +
        "        \"padding-left\":1,\n" +
        "        \"padding-right\":1,\n" +
        "        \"padding-top\":10,\n" +
        "        \"text\":\"this is my first popup\",\n" +
        "        \"text-align\":\"left\"\n" +
        "     },\n" +
        "     \"image\":[\n" +
        "        \n" +
        "     ],\n" +
        "     \"interactions\":[\n" +
        "        \n" +
        "     ],\n" +
        "     \"meta\":{\n" +
        "        \"animation\":{\n" +
        "           \"transition-in\":{\n" +
        "              \"type\":\"none\"\n" +
        "           },\n" +
        "           \"transition-out\":{\n" +
        "              \"type\":\"none\"\n" +
        "           }\n" +
        "        },\n" +
        "        \"background-color\":\"#F9F9F9\",\n" +
        "        \"box-shadow\":0,\n" +
        "        \"container_type\":\"modal\",\n" +
        "        \"dismiss-params\":{\n" +
        "           \"touch-in\":true,\n" +
        "           \"touch-out\":true\n" +
        "        },\n" +
        "        \"display-params\":{\n" +
        "           \"_height_unit\":\"px\",\n" +
        "           \"_width_unit\":\"%\",\n" +
        "           \"border-color\":\"#E0E0E0\",\n" +
        "           \"border-width\":1,\n" +
        "           \"corner-radius\":5,\n" +
        "           \"width\":\"80%\"\n" +
        "        },\n" +
        "        \"dynamicUrl\":{\n" +
        "           \"display_condition\":\"any_page\",\n" +
        "           \"matching_condition\":{\n" +
        "              \"conditions\":[\n" +
        "                 {\n" +
        "                    \"operator\":\"equal\"\n" +
        "                 }\n" +
        "              ],\n" +
        "              \"match_type\":\"any\"\n" +
        "           }\n" +
        "        },\n" +
        "        \"id\":10,\n" +
        "        \"margin-bottom\":0,\n" +
        "        \"margin-left\":0,\n" +
        "        \"margin-right\":0,\n" +
        "        \"margin-top\":0,\n" +
        "        \"padding-bottom\":10,\n" +
        "        \"padding-left\":10,\n" +
        "        \"padding-right\":10,\n" +
        "        \"padding-top\":10,\n" +
        "        \"placement\":\"center\",\n" +
        "        \"tool\":\"modal\",\n" +
        "        \"view\":\"_ALL_\"\n" +
        "     },\n" +
        "     \"overlay\":{\n" +
        "        \n" +
        "     },\n" +
        "     \"screenshot\":{\n" +
        "        \"client_version\":\"Unknown\",\n" +
        "        \"height\":568,\n" +
        "        \"id\":\"_ALL_\",\n" +
        "        \"json\":[\n" +
        "           \n" +
        "        ],\n" +
        "        \"model\":\"Simulator\",\n" +
        "        \"modified\":\"Unknown\",\n" +
        "        \"orientation\":\"portrait\",\n" +
        "        \"view\":\"_ALL_\",\n" +
        "        \"width\":320\n" +
        "     },\n" +
        "     \"templateId\":213,\n" +
        "     \"theme\":\"popup\",\n" +
        "     \"title\":{\n" +
        "        \"color\":\"#000000\",\n" +
        "        \"font-size\":20,\n" +
        "        \"font-weight\":\"bold\",\n" +
        "        \"text-align\":\"left\"\n" +
        "     }\n" +
        "}"




    override fun inflateAndSetContentView() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pattern = "dd-MMM-yyyy hh:mm:ss"
        @SuppressLint("SimpleDateFormat") val simpleDateFormat = SimpleDateFormat(pattern)
        Contextual.init(this.application, getString(R.string.app_key), object : CtxEventObserver{
            override fun onInstallRegistered(installId: UUID, context: Context) {
                val date = simpleDateFormat.format(Date())
                tagStringArray(mutableMapOf("sh_email" to "qa@contextu.al.com",
                    "sh_first_name" to "QA", "sh_last_name" to "Contextual",
                    "sh_cuid" to "pz-wiki-dev-user - ${BuildConfig.CTX_VERSION_NAME} - $date"))
            }

            override fun onInstallRegisterError(errorMsg: String) {
                Log.e("Integration", errorMsg)
            }

        })
       // Contextual.addGuide(guideJson)
        setShortcuts(this)
        setImageZoomHelper()
     /*   if (Prefs.isInitialOnboardingEnabled() && savedInstanceState == null) {
            // Updating preference so the search multilingual tooltip
            // is not shown again for first time users
            Prefs.setMultilingualSearchTutorialEnabled(false)

            // Use startActivityForResult to avoid preload the Feed contents before finishing the initial onboarding.
            // The ACTIVITY_REQUEST_INITIAL_ONBOARDING has not been used in any onActivityResult
            startActivityForResult(InitialOnboardingActivity.newIntent(this), Constants.ACTIVITY_REQUEST_INITIAL_ONBOARDING)
        }*/
        setNavigationBarColor(ResourceUtil.getThemedColor(this, R.attr.nav_tab_background_color))
        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.mainToolbar.navigationIcon = null
    }

    override fun onResume() {
        super.onResume()
        invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        fragment.requestUpdateToolbarElevation()
        val tabsItem = menu.findItem(R.id.menu_tabs)
        if (WikipediaApp.getInstance().tabCount < 1 || fragment.currentFragment is SuggestedEditsTasksFragment) {
            tabsItem.isVisible = false
            tabCountsView = null
        } else {
            tabsItem.isVisible = true
            tabCountsView = TabCountsView(this, null)
            tabCountsView!!.setOnClickListener {
                if (WikipediaApp.getInstance().tabCount == 1) {
                    startActivity(PageActivity.newIntent(this@MainActivity))
                } else {
                    startActivityForResult(TabActivity.newIntent(this@MainActivity), Constants.ACTIVITY_REQUEST_BROWSE_TABS)
                }
            }
            tabCountsView!!.updateTabCount(showTabCountsAnimation)
            tabCountsView!!.contentDescription = getString(R.string.menu_page_show_tabs)
            tabsItem.actionView = tabCountsView
            tabsItem.expandActionView()
            FeedbackUtil.setButtonLongPressToast(tabCountsView!!)
            showTabCountsAnimation = false
        }
        return true
    }

    override fun createFragment(): MainFragment {
        return MainFragment.newInstance()
    }

    override fun onTabChanged(tab: NavTab) {
        if (tab == NavTab.EXPLORE) {
            binding.mainToolbarWordmark.visibility = View.VISIBLE
            binding.mainToolbar.title = ""
            controlNavTabInFragment = false
        } else {
            if (tab == NavTab.SEARCH && Prefs.shouldShowSearchTabTooltip()) {
                FeedbackUtil.showTooltip(this, fragment.binding.mainNavTabLayout.findViewById(NavTab.SEARCH.id()), getString(R.string.search_tab_tooltip), aboveOrBelow = true, autoDismiss = false)
                Prefs.setShowSearchTabTooltip(false)
            }
            binding.mainToolbarWordmark.visibility = View.GONE
            binding.mainToolbar.setTitle(tab.text())
            controlNavTabInFragment = true
        }
        fragment.requestUpdateToolbarElevation()
    }

    override fun updateTabCountsView() {
        showTabCountsAnimation = true
        invalidateOptionsMenu()
    }

    override fun onSupportActionModeStarted(mode: ActionMode) {
        super.onSupportActionModeStarted(mode)
        if (!controlNavTabInFragment) {
            fragment.setBottomNavVisible(false)
        }
    }

    override fun onSupportActionModeFinished(mode: ActionMode) {
        super.onSupportActionModeFinished(mode)
        fragment.setBottomNavVisible(true)
    }

    override fun updateToolbarElevation(elevate: Boolean) {
        if (elevate) {
            setToolbarElevationDefault()
        } else {
            clearToolbarElevation()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        fragment.handleIntent(intent)
    }

    override fun onGoOffline() {
        fragment.onGoOffline()
    }

    override fun onGoOnline() {
        fragment.onGoOnline()
    }

    override fun onBackPressed() {
        if (fragment.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    fun isCurrentFragmentSelected(f: Fragment): Boolean {
        return fragment.currentFragment === f
    }

    fun getToolbar(): Toolbar {
        return binding.mainToolbar
    }

    private fun setToolbarElevationDefault() {
        binding.mainToolbar.elevation = DimenUtil.dpToPx(DimenUtil.getDimension(R.dimen.toolbar_default_elevation))
    }

    private fun clearToolbarElevation() {
        binding.mainToolbar.elevation = 0f
    }

    companion object {
        @JvmStatic
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

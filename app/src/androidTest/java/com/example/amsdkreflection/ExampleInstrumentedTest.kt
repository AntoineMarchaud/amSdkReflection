package com.example.amsdkreflection

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.amsdkreflection.sdk.CsAnalytics
import com.example.amsdkreflection.sdk.CustomReader
import com.google.common.truth.Truth.assertThat
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var mActivity: MainActivity
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter
    private var itemCount = 0

    private lateinit var mCustomReader: CustomReader

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUpTest() {
        /* obtaining the Activity from the ActivityScenarioRule */
        activityScenarioRule.scenario.onActivity { activity: MainActivity ->
            mActivity = activity
            mRecyclerView = mActivity.findViewById(R.id.recycler_view)
            mAdapter = mActivity.myAdapter
            itemCount = mAdapter.itemCount
            mCustomReader = CustomReader(mActivity)
        }
    }

    @Test
    fun recyclerViewOneRandomClick() {

        if (itemCount > 0) {

            for (x in 0..5) {

                val i = Random.nextInt(0, Model.MAX)

                Espresso.onView(ViewMatchers.withId(R.id.recycler_view))
                        .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(i, ViewActions.click()))

                // wait debounce
                Espresso.onView(isRoot()).perform(wait(CsAnalytics.DELAY + 50L))

                // check file exists
                val fis = FileInputStream(File(mActivity.filesDir, CsAnalytics.LOG_FILE_NAME))
                assertThat(fis).isNotNull()
                fis.close()

                // check content
                val line = mCustomReader.getNewLine()
                if(line != null) {
                    // check position
                    assertThat(line).contains("Position: $i.")

                    val entry = mActivity.myCsAnalytics.getEntry(i)
                    assertThat(entry).isNotNull()

                    val nbBefore = mActivity.myCsAnalytics.getNumberAnimalsBefore(entry!!.value, i)
                    assertThat(nbBefore).isAtLeast(0)
                    assertThat(nbBefore).isAtMost(x)

                    // check number before + type
                    assertThat(line).contains("There are $nbBefore ${entry.key}.")
                }
            }
        }

        mCustomReader.clear()
    }


    /** Perform action of waiting for a specific view id.  */
    fun wait(millis: Long): ViewAction {

        return object : ViewAction {

            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific during $millis millis."
            }

            override fun perform(uiController: UiController, view: View?) {

                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis

                do {
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)
            }
        }
    }
}
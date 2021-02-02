package com.example.amsdkreflection

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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
        }
    }

    @Test
    fun recyclerViewOneRandomClick() {

        if (itemCount > 0) {

            for(x in 0..5) {

                val i = Random.nextInt(0, Model.MAX)

                Espresso.onView(ViewMatchers.withId(R.id.recycler_view))
                        .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(i, ViewActions.click()))

                Thread.sleep(Random.nextLong(0, 10) * 1000L)
            }

            // how checking file exist ?

            // how checking the content ?

            // how to check the 2 seconds before toast ?
        }
    }

    @Test
    fun recyclerViewFixedClick() {

        // click on the item
        //onData(allOf(`is`(instanceOf(Map::class.java)), hasEntry(equalTo("STR"), `is`("item: 50")))).perform(click())
    }
}
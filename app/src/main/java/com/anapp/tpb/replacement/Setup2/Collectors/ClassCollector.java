package com.anapp.tpb.replacement.Setup2.Collectors;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.anapp.tpb.replacement.Home.Utilities.TimeUtils;
import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup2.Input.ClassInput;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;

import java.util.ArrayList;
import java.util.Collections;
//The problem happens because the fragments are created at the same time, the current one has the right day, and the others don't



/**
 * Created by theo on 21/04/16.
 */
public class ClassCollector extends AppCompatActivity {
    private static final String TAG = "ClassCollector";
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    private DataHelper dataHelper;
    private DayPagerAdapter mPagerAdapter;
    private FloatingActionButton addClassFab;
    private FloatingActionButton nextFab;
    private Class nextWindow;
    private int day;
    
    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_input);
        dataHelper = new DataHelper(this);
        mPagerAdapter = new DayPagerAdapter(this, getSupportFragmentManager());
        final ViewPager mPager = (ViewPager) findViewById(R.id.container);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected (int position) {
                if(position != day && position == 4) {
                    updateFab(true);
                } else if(day == 4) {
                    updateFab(false);
                }
                day = position;
            }

            @Override
            public void onPageScrollStateChanged (int state) {

            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);
        addClassFab = (FloatingActionButton) findViewById(R.id.fab);
        addClassFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(getApplicationContext(), ClassInput.class);
                ClassDayFragment cdf = mPagerAdapter.getFragment(day);
                i.putExtra("subjects", cdf.mAdapter.subjects);
                i.putExtra("classes", cdf.mAdapter.classesForDay);
                i.putExtra("day", cdf.day);
                startActivityForResult(i, 1);
            }
        });

        nextFab = (FloatingActionButton) findViewById(R.id.fab_finish_add_class);
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(getApplicationContext(), nextWindow);
                startActivity(i);
            }
        });
        nextWindow = (Class) getIntent().getSerializableExtra("nextWindow");
    }

    private void updateFab(boolean done) {
        //DP conversion  http://stackoverflow.com/questions/30202379/android-views-gettop-getleft-getx-gety-getwidth-getheight-meth
        final float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics()); //Height of fab + fab margin into pixels
        TranslateAnimation move;
        if(done) {
            move = new TranslateAnimation(0, 0, 0, -px);
            move.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    addClassFab.setY(nextFab.getY() - px);
                    //The animation below stops the flickering/jump once the animation completes. No idea why
                    animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    animation.setDuration(1);
                    addClassFab.startAnimation(animation);
                }
            });
            move.setDuration(300);
            addClassFab.startAnimation(move);
            nextFab.setVisibility(View.VISIBLE);
        } else {
            move = new TranslateAnimation(0, 0, 0, px);
            move.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    addClassFab.setY(nextFab.getY());
                    animation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f);
                    animation.setDuration(1);
                    addClassFab.startAnimation(animation);
                }
            });
            move.setDuration(300);
            addClassFab.startAnimation(move);
            nextFab.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            ClassTime classTime = (ClassTime) data.getSerializableExtra("class");
            boolean edited = data.getBooleanExtra("edited", false);
            if(edited) {
                Log.i(TAG, "Edited class returned " + classTime);
                mPagerAdapter.getDayFragment(classTime.getDay()).mAdapter.updateClassValue(classTime);
            } else {
            Log.i(TAG, "New class returned " + classTime);
                mPagerAdapter.getDayFragment(classTime.getDay()).mAdapter.addClass(classTime);
            }
        }
    }


    public static class ClassDayFragment extends Fragment {
        private RecyclerView mRecyclerView;
        private ClassCollectorAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private DataHelper dataHelper;
        private ClassCollector parent;
        private int day;

        public ClassDayFragment() {}

        public static ClassDayFragment newInstance(ClassCollector parent, int day) {
            Log.i(TAG, "Fragment created with day of " + day);
            ClassDayFragment fragment = new ClassDayFragment();
            fragment.day = day;
            fragment.parent = parent;
            fragment.dataHelper = parent.dataHelper;
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_class_input, container, false);
            mAdapter = new ClassCollectorAdapter(parent, dataHelper,  day);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_class_today);
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager = new LinearLayoutManager(parent);
            mRecyclerView.setLayoutManager(mLayoutManager);
            return  rootView;
        }

        @Override
        public String toString () {
            return "Day fragment " + day;
        }
    }

    public static class ClassCollectorAdapter extends RecyclerView.Adapter<ClassCollectorAdapter.ClassViewHolder> {
        private static final String TAG = "ClassCollectorAdapter";
        private ClassCollector parent;
        private DataHelper dataHelper;
        private ArrayList<Subject> subjects;
        private ArrayList<ClassTime> classesForDay;

        public ClassCollectorAdapter (ClassCollector parent, DataHelper dataHelper, int day) {
            this.parent = parent;
            this.dataHelper = dataHelper;
            this.subjects = dataHelper.getAllSubjects();
            this.classesForDay = dataHelper.getClassesForDay(day);
            Log.i(TAG, "Adapter created for day " + day);
        }

        public void updateClass(ClassTime ct) {
            Intent i = new Intent(parent.getApplicationContext(), ClassInput.class);
            i.putExtra("editingClass", ct);
            i.putExtra("subjects", subjects);
            i.putExtra("classes", dataHelper.getClassesForDay(ct.getDay()));
            i.putExtra("day", ct.getDay());
            Log.i(TAG, "Updating class " + ct);
            parent.startActivityForResult(i, 1);
        }

        public void updateClassValue(ClassTime c) {
            dataHelper.updateClass(c);
            Log.i(TAG, "Updating class with values of " + c);
            classesForDay.set(classesForDay.indexOf(c), c);
            Log.i(TAG, "Classes available " + classesForDay + " Position is " + classesForDay.indexOf(c));
            Collections.sort(classesForDay);
            notifyItemChanged(classesForDay.indexOf(c));
        }

        public void addClass(ClassTime c) {
            c = dataHelper.addClass(c);
            classesForDay.add(c);
            Collections.sort(classesForDay);
            notifyItemInserted(classesForDay.indexOf(c));
        }

        public void delete(int position) {
            dataHelper.deleteClass(classesForDay.get(position));
            classesForDay.remove(position);
            Log.i(TAG, "Deleting item at position " + position);
            Log.i(TAG, "Classes are now " + classesForDay);
            notifyItemRemoved(position);
        }

        @Override
        public ClassViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_class, parent, false);
            return new ClassViewHolder(v, this);
        }

        @Override
        public void onBindViewHolder (final ClassViewHolder holder, final int position) {
            ClassTime ct = classesForDay.get(position);
            holder.ct = ct;
            Subject s = new Subject();
            Log.i(TAG, "Subjects: " + subjects);
            for (Subject si : subjects) {
                if (ct.getSubjectID() == si.getId()) {
                    s = si;
                    break;
                }
            }
            Log.i(TAG, "Binding viewholder for class " + ct.toString() + " and subject " + s.toString());
            holder.className.setText(s.getName());
            holder.colourBar.setBackgroundColor(s.getColor());
            int start = ct.getStart();
            int end = ct.getEnd();
            String timeRange = TimeUtils.format(start) + " to " + TimeUtils.format(end);
            holder.classTime.setText(timeRange);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(holder.getAdapterPosition());
                }
            });

        }

        @Override
        public int getItemCount () {
            return classesForDay.size();
        }

        public static class ClassViewHolder extends RecyclerView.ViewHolder {
            private static ClassCollectorAdapter parent;
            private ClassTime ct;
            private TextView className;
            private TextView classTime;
            private ImageButton deleteButton;
            private View colourBar;

            public ClassViewHolder(View v, ClassCollectorAdapter p) {
                super(v);
                setIsRecyclable(false);
                parent = p;
                className = (TextView) v.findViewById(R.id.text_subject_name);
                classTime = (TextView) v.findViewById(R.id.text_class_time);
                deleteButton = (ImageButton) v.findViewById(R.id.button_delete);
                colourBar = v.findViewById(R.id.colour_bar);
                //Adding listener to the entire view, in order to allow editing
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.updateClass(ct);
                    }
                });
            }
        }
    }


    public static class DayPagerAdapter extends FragmentPagerAdapter {
        private static final String TAG = "DayPagerAdapter";
        private ClassCollector parent;
        private ClassDayFragment[] fragments;

        public DayPagerAdapter(ClassCollector parent, FragmentManager fm) {
            super(fm);
            this.parent = parent;
            fragments = new ClassDayFragment[5];
        }


        @Override
        public Fragment getItem (int position) {
            //Day is incremented here and only here
            //Apparently these positions start from 1???
            Log.i(TAG, "Creating new fragment with position " + position);
            ClassDayFragment fragment = ClassDayFragment.newInstance(parent, position+2);
            fragments[position] = fragment;
            return fragment;
        }

        public ClassDayFragment getFragment(int position) {
            return fragments[position];
        }

        public ClassDayFragment getDayFragment(int day) {
            Log.i(TAG, "Returning fragment with day of " + fragments[day-2].day +
            " when current position is " + day);
            return fragments[day-2];
        }

        @Override
        public int getCount () {
            return DAYS.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return DAYS[position];
        }
    }

}

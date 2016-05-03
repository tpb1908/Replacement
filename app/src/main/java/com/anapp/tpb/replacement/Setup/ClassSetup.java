package com.anapp.tpb.replacement.Setup;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Storage.DataHelper;
import com.anapp.tpb.replacement.Storage.TableTemplates.ClassTime;
import com.anapp.tpb.replacement.Storage.TableTemplates.Subject;
import com.klinker.android.sliding.SlidingActivity;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by theo on 12/04/16.
 */
public class ClassSetup extends AppCompatActivity {
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri"};
    private DataHelper dataHelper;
    private DayPagerAdapter mPagerAdapter;
    private ViewPager mPager;
    private FloatingActionButton addClassFab;
    private FloatingActionButton nextFab;
    private Class nextWindow;


    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_input);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Class times");
        setSupportActionBar(toolbar);

        dataHelper = new DataHelper(this);

        mPagerAdapter = new DayPagerAdapter(this, getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.container);
        mPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        addClassFab = (FloatingActionButton) findViewById(R.id.fab);
        addClassFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Intent i = new Intent(getApplicationContext(), ClassInputSlider.class);
                ClassDayFragment cdf = mPagerAdapter.getCurrentFragment();
                i.putExtra("subjects", cdf.mAdapter.subjects);
                i.putExtra("classes", cdf.mAdapter.classes);
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
            int page = classTime.getDay() -2;
            boolean edited = data.getBooleanExtra("editing", false);
            if(edited) {
                dataHelper.updateClass(classTime);
                mPagerAdapter.getCurrentFragment().mAdapter.updateClassValue(classTime);
            } else {
                classTime = dataHelper.addClass(classTime);
                mPagerAdapter.getCurrentFragment().mAdapter.addClass(classTime);
            }
        }
    }

    public static class ClassDayFragment extends Fragment {

        private RecyclerView mRecyclerView;
        private ClassSetupAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private DataHelper dataHelper;
        private ClassSetup parent;
        private int day;

        public ClassDayFragment() {}

        public static ClassDayFragment newInstance(ClassSetup parent, int day) {
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
            mAdapter = new ClassSetupAdapter(parent, dataHelper,  day);
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_class_today);
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager = new LinearLayoutManager(parent);
            mRecyclerView.setLayoutManager(mLayoutManager);
            return  rootView;
        }
    }



    public static class ClassSetupAdapter extends RecyclerView.Adapter<ClassSetupAdapter.ClassViewHolder> {
        private final int day;
        private ClassSetup parent;
        private DataHelper dataHelper;
        private ArrayList<Subject> subjects;
        private ArrayList<ClassTime> classes;

        public ClassSetupAdapter (ClassSetup parent, DataHelper dataHelper, int day) {
            this.day = day;
            this.parent = parent;
            this.dataHelper = dataHelper;
            //TODO- Add optimisations to dataHelper
            this.subjects = dataHelper.getAllSubjects();
            this.classes = dataHelper.getClassesForDay(day);

        }




        //Put the classes in here, not in the parent
        public void openClass(int postition) {
            Log.i("Opening class", "Opening class at " + postition);
        }

        public void updateClassValue(ClassTime c) {
            classes.set(classes.indexOf(c), c);
            Collections.sort(classes);
            notifyItemChanged(classes.indexOf(c));
        }

        public void addClass(ClassTime c) {
            classes.add(c);
            Collections.sort(classes);
            notifyItemInserted(classes.indexOf(c));
            Log.d("Classes",""+ classes);
        }

        public void delete(int position) {
            dataHelper.deleteClass(classes.get(position));
            notifyItemRemoved(position);
            classes.remove(position);
        }

        @Override
        public ClassViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_class, parent, false);
            return new ClassViewHolder(v, this);
        }

        @Override
        public void onBindViewHolder (final ClassViewHolder holder, int position) {
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete(holder.getAdapterPosition());
                }
            });
            ClassTime ct = classes.get(position);
            String timeRange;
            Subject s = new Subject();
            for (Subject si : subjects) {
                if (ct.getSubjectID() == si.getId()) {
                    s = si;
                    break;
                }
            }
            holder.className.setText(s.getName());
            holder.colourBar.setBackgroundColor(s.getColor());
            int start = ct.getStart();
            int end = ct.getEnd();
            if (start < 1000) {
                timeRange = (Integer.toString(start).substring(0, 1) + ":" + Integer.toString(start).substring(1));
            } else {
                timeRange = (Integer.toString(start).substring(0, 2) + ":" + Integer.toString(start).substring(2));
            }
            if (end < 1000) {
                timeRange += " to " + (Integer.toString(end).substring(0, 1) + ":" + Integer.toString(end).substring(1));
            } else {
                timeRange += " to " + (Integer.toString(end).substring(0, 2) + ":" + Integer.toString(end).substring(2));
            }
            holder.classTime.setText(timeRange);

        }

        @Override
        public int getItemCount () {
            return classes.size();
        }

        public static class ClassViewHolder extends RecyclerView.ViewHolder {
            private static ClassSetupAdapter parent;
            private TextView className;
            private TextView classTime;
            private ImageButton deleteButton;
            private View colourBar;

            public ClassViewHolder(View v, ClassSetupAdapter p) {
                super(v);
                setIsRecyclable(false);
                parent = p;
                className = (TextView) v.findViewById(R.id.text_class_past_info);
                classTime = (TextView) v.findViewById(R.id.text_class_time);
                deleteButton = (ImageButton) v.findViewById(R.id.button_delete);
                colourBar = v.findViewById(R.id.colour_bar);
                //Adding listener to the entire view, in order to allow editing
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.openClass(getAdapterPosition());
                    }
                });
            }
        }
    }



    public static class DayPagerAdapter extends FragmentPagerAdapter {
        private ClassSetup parent;
        private ArrayList<ClassDayFragment> fragments;
        private int currentPosition;

        public DayPagerAdapter(ClassSetup parent, FragmentManager fm) {
            super(fm);
            this.parent = parent;
            fragments = new ArrayList<>(getCount());
        }

        @Override
        public void setPrimaryItem (ViewGroup container, int position, Object object) {
            if(this.currentPosition != position) {
                if (position == 4) {
                    parent.updateFab(true);
                } else if(currentPosition == 4) { //The position before the updateCurrent was Friday
                    parent.updateFab(false);
                }
                this.currentPosition = position;
            }
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public Fragment getItem (int position) {
            //Day is incremented here and only here
            //Apparently these positions start from 1???
            ClassDayFragment fragment = ClassDayFragment.newInstance(parent, position+2);
            fragments.add(fragment);
            return fragment;
        }

        public ClassDayFragment getCurrentFragment() {
            return fragments.get(currentPosition);
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

    public static class ClassInputSlider extends SlidingActivity {
        private ArrayList<ClassTime> classesForDay;
        private ArrayList<Subject> subjects;
        private Spinner lessonSpinner;
        private ClassTime current;
        private int start = -1;
        private int end = -1;
        private boolean editing;
        private EditText startTime;
        private EditText endTime;
        private int day;

        public ClassInputSlider () {
        }

        public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
            super.onCreate(savedInstanceState, persistentState);
        }

        @Override
        public void init (Bundle savedInstanceState) {
            setContent(R.layout.input_class);
            setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
            enableFullscreen();
            lessonSpinner = (Spinner) findViewById(R.id.spinner_subject);
            startTime = (EditText) findViewById(R.id.edittext_start_time);
            endTime = (EditText) findViewById(R.id.editttext_end_time);
            subjects = (ArrayList<Subject>) getIntent().getSerializableExtra("subjects");
            classesForDay = (ArrayList<ClassTime>) getIntent().getSerializableExtra("classes");
            day = getIntent().getIntExtra("day", 0);
            int spinnerDefaultIndex = 0;
            try {
                current = (ClassTime) getIntent().getSerializableExtra("editingClass");
                start = current.getStart();
                end = current.getEnd();
                String time; //Recommended to perform string concatenation outside of setText() method
                //Time is stored in a 0000 2359 format. If hour < 10, then time < 100 -> String must be split differently
                if (start < 1000) {
                    time = (Integer.toString(start).substring(0, 1) + ":" + Integer.toString(start).substring(1));
                } else {
                    time = (Integer.toString(start).substring(0, 2) + ":" + Integer.toString(start).substring(2));
                }
                startTime.setText(time);
                if (end < 1000) {
                    time = (Integer.toString(end).substring(0, 1) + ":" + Integer.toString(end).substring(1));
                } else {
                    time = (Integer.toString(end).substring(0, 2) + ":" + Integer.toString(end).substring(2));
                }
                for(Subject s : subjects) {
                    if(s.getId() == current.getSubjectID()) {
                        spinnerDefaultIndex = subjects.indexOf(s);
                        break;
                    }
                }
                endTime.setText(time);
                editing = true;
            } catch (Exception e) {
                editing = false;
            }
            lessonSpinner.setAdapter(new SpinnerAdapter(subjects));
            lessonSpinner.setSelection(spinnerDefaultIndex);


            startTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    displayTimePicker(true);
                }
            });
            endTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    displayTimePicker(false);
                }
            });

            FloatingActionButton.OnClickListener fabListener = new View.OnClickListener() {
                @Override
                public void onClick (View v) {
                    Log.d("Spinner",""+lessonSpinner.getSelectedItemPosition());
                    ClassTime classTime = new ClassTime();
                    if(start != -1 && end != -1) {
                        classTime.setStart(start);
                        classTime.setEnd(end);
                        classTime.setDay(day);
                        classTime.setSubjectID(subjects.get(lessonSpinner.getSelectedItemPosition()).getId());
                        if(!checkOverlap(classTime)) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("edited", editing);
                            returnIntent.putExtra("class", classTime);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ClassInputSlider.this, R.style.DialogTheme);
                        builder.setTitle("Missing time")
                                .setMessage("Please input both start and end times")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            };

            if (editing) {
                setTitle("Edit Class");
            } else {
                setTitle("New Class");
            }

            setFab(getResources().getColor(R.color.colorAccent), R.drawable.fab_icon_tick, fabListener);
        }

        //FIXME- Boolean inversion
        private boolean checkOverlap(ClassTime toCheck) {
            ClassTime overlap = null;
            boolean foundOverlap = false;
            for(ClassTime c : classesForDay) {
                //See here -http://stackoverflow.com/a/325964/4191572  > used rather than >= as lessons times may be continuous
                if (c.getStart() < toCheck.getEnd() && c.getEnd() > toCheck.getStart()) {
                    overlap = c;
                    foundOverlap = true;
                    Log.d("Data", "Overlap found " + c.toString());
                    break;
                }
            }
            if(foundOverlap) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ClassInputSlider.this, R.style.DialogTheme);
                builder.setTitle("Overlap with another class")
                        .setMessage("There is an overlap with another class from " + overlap.getStart() + " to "
                        + overlap.getEnd())
                        .setPositiveButton("OK", null)
                        .show();
            }
            return foundOverlap;

        }

        private void displayTimePicker(final boolean startEnd) {
            TimePickerDialog mTimePicker;
            //Creating dialog
            mTimePicker = new TimePickerDialog(ClassInputSlider.this, R.style.DatePickerTheme, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Log.d("Data", "Time received- Hour of " + hourOfDay + " Minute of " + minute);
                    String output = hourOfDay + ":";
                    //Formatting the string
                    if (minute < 10) {
                        output += "0" + minute;
                    } else {
                        output += minute;
                    }
                    if (startEnd) {
                        startTime.setText(output);
                        start = (hourOfDay * 100) + minute;
                    } else {
                        endTime.setText(output);
                        end = (hourOfDay * 100) + minute;
                    }
                }
            }, 9, 0, true); //Use 24 hour time. Default of 9am
            if (startEnd) {
                mTimePicker.setTitle("Select start time");
            } else {
                mTimePicker.setTitle("Select end time");
            }
            mTimePicker.show();
        }

        private class SpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {
            private ArrayList<Subject> subjects;


            public SpinnerAdapter(ArrayList<Subject> subjects) {
                super();
                this.subjects = subjects;
            }

            @Override
            public int getCount () {
                return subjects.size();
            }

            @Override
            public Object getItem (int position) {
                return null;
            }

            @Override
            public long getItemId (int position) {
                return 0;
            }

            @Override
            public View getView (int position, View convertView, ViewGroup parent) {
                return getCustomView(position, parent);
            }

            @Override
            public View getDropDownView (int position, View convertView, ViewGroup parent) {
                return getCustomView(position, parent);
            }

            public View getCustomView(int position, ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View row = inflater.inflate(R.layout.listitem_subject_spinner, parent, false); //False is important. It indicates whether the view should be added directly to the ViewGroup
                TextView name = (TextView) row.findViewById(R.id.text_subject);
                View colourBar = row.findViewById(R.id.colour_bar);
                String text = this.subjects.get(position).getName() + ", " + this.subjects.get(position).getTeacher();
                name.setText(text);
                colourBar.setBackgroundColor(this.subjects.get(position).getColor());
                return row;
            }
        }
    }
}

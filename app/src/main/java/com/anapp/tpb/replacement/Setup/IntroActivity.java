package com.anapp.tpb.replacement.Setup;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.anapp.tpb.replacement.R;
import com.anapp.tpb.replacement.Setup.DataPresentation.FragmentCreator;
import com.anapp.tpb.replacement.Setup.DataPresentation.SubjectCollector;
import com.anapp.tpb.replacement.Setup.DataPresentation.TermDateCollector;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {
    FragmentCreator first;

    @Override
    public void onSkipPressed() {
    }

    @Override
    public void init(Bundle savedInstanceState) {
        showSkipButton(false);
        showStatusBar(false);
        setSeparatorColor(getResources().getColor(R.color.colorDivider));
        first = FragmentCreator.newInstance(R.layout.name_collection);
        addSlide(first);

        addSlide(AppIntroFragment.newInstance("Test slide 1", "Change this", R.drawable.fab_plus_icon, R.color.colorAccent));
        addSlide(AppIntroFragment.newInstance("Test slide 2", "Change this as well", R.drawable.fab_plus_icon, R.color.colorAccent));
    }

    @Override
    public void onDonePressed() {
        Intent i = new Intent(getApplicationContext(), TermDateCollector.class);
        i.putExtra("nextWindow", SubjectCollector.class);
        startActivity(i);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void onSlideChanged() {

    }

    @Override
    public void onNextPressed() {


    }

    @Override
    public boolean onKeyDown(int code, KeyEvent keyEvent) {
        return true;
    }
}

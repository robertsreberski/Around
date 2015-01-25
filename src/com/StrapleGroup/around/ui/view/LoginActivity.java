package com.StrapleGroup.around.ui.view;

import android.animation.ValueAnimator;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.ui.utils.FuckTextLayout;
import com.StrapleGroup.around.ui.view.fragments.LoginFragment;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import java.util.Arrays;

public class LoginActivity extends FragmentActivity implements Constants, Session.StatusCallback {
    private Context context;
    private Button loginButton = null;
    private LoginButton facebookLogin;
    private SignInButton googleLogin;
    private FuckTextLayout loginContainer = null;
    private MyGestureDetector gestureListener = new MyGestureDetector();
    private float width;
    private float height;
    private UiLifecycleHelper fbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        fbHelper = new UiLifecycleHelper(this, this);
        fbHelper.onCreate(savedInstanceState);
        loginButton = (Button) findViewById(R.id.go_register);
        GestureDetector gestureDetector = new GestureDetector(this, gestureListener);
        loginContainer = (FuckTextLayout) findViewById(R.id.login_container);
        facebookLogin = (LoginButton) findViewById(R.id.facebook_login);
        facebookLogin.setReadPermissions(Arrays.asList("email", "public_profile"));
        googleLogin = (SignInButton) findViewById(R.id.google_login);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        loginContainer.setOnTouchListener(new TouchEvents());

    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i("facebook", "Logged in...");
        } else if (state.isClosed()) {
            Log.i("facebook", "Logged out...");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        fbHelper.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fbHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fbHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        fbHelper.onSaveInstanceState(outState);
    }

    public void goRegister(View v) {
        Intent pGoRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(pGoRegister);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void call(Session session, SessionState state, Exception exception) {
        onSessionStateChange(session, state, exception);
    }

    public class TouchEvents implements View.OnTouchListener {
        float startY = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) loginContainer.getLayoutParams();
                    float y = event.getRawY();
                    int deltaY = (int) (y - startY);
                    float pHeight = loginContainer.getHeight();

                    params.height = (int) pHeight - deltaY;

                    loginButton.setAlpha(1 - (pHeight / (height / 2)));
                    facebookLogin.setAlpha(1 - (pHeight / (height / 2)));
                    googleLogin.setAlpha(1 - (pHeight / (height / 2)));

                    ValueAnimator pAnim = ValueAnimator.ofFloat(pHeight, (float) params.height);
                    pAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            loginContainer.setLayoutParams(params);
                        }
                    });
                    pAnim.setTarget(loginContainer);
                    pAnim.start();
                    startY = y;
                    return true;

                case MotionEvent.ACTION_DOWN:
                    loginButton.setEnabled(false);
                    facebookLogin.setEnabled(false);
                    googleLogin.setEnabled(false);

                    startY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    RelativeLayout.LayoutParams paramsUp = (RelativeLayout.LayoutParams) loginContainer.getLayoutParams();
                    if (paramsUp.height < height / 5) {

                        ValueAnimator pAnim2 = ValueAnimator.ofFloat((float) paramsUp.height, 220);
                        pAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                paramsUp.height = 220;

                                loginButton.setEnabled(true);
                                facebookLogin.setEnabled(true);
                                googleLogin.setEnabled(true);
                                loginButton.setAlpha(1);
                                facebookLogin.setAlpha(1);
                                googleLogin.setAlpha(1);
                                loginContainer.setLayoutParams(paramsUp);
                            }
                        });
                        pAnim2.setTarget(loginContainer);
                        pAnim2.start();
                    } else {
                        ValueAnimator pAnim2 = ValueAnimator.ofFloat((float) paramsUp.height, height / 2);
                        pAnim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                paramsUp.height = (int) height / 2;
                                loginContainer.setLayoutParams(paramsUp);
                                loginButton.setAlpha(0);
                                facebookLogin.setAlpha(0);
                                googleLogin.setAlpha(0);
                            }
                        });
                        pAnim2.setTarget(loginContainer);
                        pAnim2.start();
                    }
                    return true;
            }
            return false;
        }

    }

    private class MyGestureDetector extends android.view.GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = -250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 2000;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) loginContainer.getLayoutParams();
                if (loginContainer.getHeight() > (height / 5)) {
                    params.height = (int) height / 2;
                    loginContainer.setLayoutParams(params);
//                    ValueAnimator pAnim = ValueAnimator.ofFloat(loginContainer.getHeight(), height / 2);
//                    pAnim.setDuration(1000);
//                    pAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//
//
//                        }
//                    });
//                    pAnim.start();
                } else {
                    params.height = 70;
                    loginContainer.setLayoutParams(params);
//                    ValueAnimator pAnim = ValueAnimator.ofFloat(loginContainer.getHeight(), 70);
//                    pAnim.setDuration(1000);
//                    pAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                        @Override
//                        public void onAnimationUpdate(ValueAnimator animation) {
//
//
//                        }
//                    });
//                    pAnim.start();
                }

            }

            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}

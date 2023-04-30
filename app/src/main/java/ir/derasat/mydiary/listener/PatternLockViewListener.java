package ir.derasat.mydiary.listener;

/**
 * Created by aritraroy on 19/03/17.
 */


import java.util.List;

import ir.derasat.mydiary.PatternLockView;

/**
 * The callback interface for detecting patterns entered by the user
 */
public interface PatternLockViewListener {

    /**
     * Fired when the pattern drawing has just started
     */
    void onStarted();

    /**
     * Fired when the pattern is still being drawn and progressed to
     * one more {@link ir.derasat.mydiary.PatternLockView.Dot}
     */
    void onProgress(List<PatternLockView.Dot> progressPattern);

    /**
     * Fired when the user has completed drawing the pattern and has moved their finger away
     * from the view
     */
    void onComplete(List<PatternLockView.Dot> pattern);

    /**
     * Fired when the patten has been cleared from the view
     */
    void onCleared();
}
package com.brandongogetap.stickyheaders;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class StickyHeaderPositionerRobot {

    private final StickyHeaderPositioner positioner;
    private View currentHeader;

    private StickyHeaderPositionerRobot() {
        StickyHeaderHandler headerHandler = mock(StickyHeaderHandler.class);
        when(headerHandler.getRecyclerParent()).thenReturn(mock(FrameLayout.class));
        when(headerHandler.getRecyclerParent().getViewTreeObserver()).thenReturn(mock(ViewTreeObserver.class));
        positioner = new StickyHeaderPositioner(headerHandler);
        positioner.setHeaderPositions(new ArrayList<Integer>());
        positioner.reset(LinearLayoutManager.VERTICAL, 0);
    }

    static StickyHeaderPositionerRobot create() {
        return new StickyHeaderPositionerRobot();
    }

    StickyHeaderPositionerRobot withHeaderPositions(List<Integer> headerPositions) {
        positioner.setHeaderPositions(headerPositions);
        return this;
    }

    StickyHeaderPositionerRobot setupPosition(int firstVisiblePosition) {
        ViewRetriever viewRetriever = mock(ViewRetriever.class);
        currentHeader = mock(View.class);
        when(currentHeader.getHeight()).thenReturn(200);
        when(viewRetriever.getViewForPosition(anyInt())).thenReturn(currentHeader);
        positioner.updateHeaderState(firstVisiblePosition, viewRetriever);
        return this;
    }

    StickyHeaderPositionerRobot reset(int firstVisiblePosition) {
        positioner.reset(LinearLayoutManager.VERTICAL, firstVisiblePosition);
        return this;
    }

    StickyHeaderPositionerRobot checkLastBoundHeaderPositionEquals(int position) {
        assertThat(positioner.getLastBoundPosition(), is(position));
        return this;
    }

    StickyHeaderPositionerRobot checkOffsetCalculation(float nextHeaderTop, float expectedOffset) {
        Map<Integer, View> visibleHeaderMap = new LinkedHashMap<>();
        View nextHeader = mock(View.class);
        visibleHeaderMap.put(6, nextHeader);
        when(nextHeader.getY()).thenReturn(nextHeaderTop);
        positioner.checkHeaderPositions(visibleHeaderMap);
        verify(currentHeader).setTranslationY(expectedOffset);
        return this;
    }
}

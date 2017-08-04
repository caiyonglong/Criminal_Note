# Criminal_Note
Android编程权威指南(第三版)的应用  
1、Criminal_Note:在CriminalIntent的基础上的挑战练习  
2、BeatBox  
3、NedLauncher  
4...

  Object used to report movement (mouse, pen, finger, trackball) events.
  Motion events may hold either absolute or relative movements and other data,
  depending on the type of device.
 
  <h3>Overview</h3>
  <p>
  Motion events describe movements in terms of an action code and a set of axis values.
  The action code specifies the state change that occurred such as a pointer going
  down or up.  The axis values describe the position and other movement properties.
  </p><p>
  For example, when the user first touches the screen, the system delivers a touch
  event to the appropriate {@link View} with the action code {@link #ACTION_DOWN}
  and a set of axis values that include the X and Y coordinates of the touch and
  information about the pressure, size and orientation of the contact area.
  </p><p>
  Some devices can report multiple movement traces at the same time.  Multi-touch
  screens emit one movement trace for each finger.  The individual fingers or
  other objects that generate movement traces are referred to as <em>pointers</em>.
  Motion events contain information about all of the pointers that are currently active
  even if some of them have not moved since the last event was delivered.
  </p><p>
  The number of pointers only ever changes by one as individual pointers go up and down,
  except when the gesture is canceled.
  </p><p>
  Each pointer has a unique id that is assigned when it first goes down
  (indicated by {@link #ACTION_DOWN} or {@link #ACTION_POINTER_DOWN}).  A pointer id
  remains valid until the pointer eventually goes up (indicated by {@link #ACTION_UP}
  or {@link #ACTION_POINTER_UP}) or when the gesture is canceled (indicated by
  {@link #ACTION_CANCEL}).
  </p><p>
  The MotionEvent class provides many methods to query the position and other properties of
  pointers, such as {@link #getX(int)}, {@link #getY(int)}, {@link #getAxisValue},
  {@link #getPointerId(int)}, {@link #getToolType(int)}, and many others.  Most of these
  methods accept the pointer index as a parameter rather than the pointer id.
  The pointer index of each pointer in the event ranges from 0 to one less than the value
  returned by {@link #getPointerCount()}.
  </p><p>
  The order in which individual pointers appear within a motion event is undefined.
  Thus the pointer index of a pointer can change from one event to the next but
  the pointer id of a pointer is guaranteed to remain constant as long as the pointer
  remains active.  Use the {@link #getPointerId(int)} method to obtain the
  pointer id of a pointer to track it across all subsequent motion events in a gesture.
  Then for successive motion events, use the {@link #findPointerIndex(int)} method
  to obtain the pointer index for a given pointer id in that motion event.
  </p><p>
  Mouse and stylus buttons can be retrieved using {@link #getButtonState()}.  It is a
  good idea to check the button state while handling {@link #ACTION_DOWN} as part
  of a touch event.  The application may choose to perform some different action
  if the touch event starts due to a secondary button click, such as presenting a
  context menu.
  </p>
 
  <h3>Batching</h3>
  <p>
  For efficiency, motion events with {@link #ACTION_MOVE} may batch together
  multiple movement samples within a single object.  The most current
  pointer coordinates are available using {@link #getX(int)} and {@link #getY(int)}.
  Earlier coordinates within the batch are accessed using {@link #getHistoricalX(int, int)}
  and {@link #getHistoricalY(int, int)}.  The coordinates are "historical" only
  insofar as they are older than the current coordinates in the batch; however,
  they are still distinct from any other coordinates reported in prior motion events.
  To process all coordinates in the batch in time order, first consume the historical
  coordinates then consume the current coordinates.
  </p><p>
  Example: Consuming all samples for all pointers in a motion event in time order.
  </p><p><pre><code>
  void printSamples(MotionEvent ev) {
      final int historySize = ev.getHistorySize();
      final int pointerCount = ev.getPointerCount();
      for (int h = 0; h &lt; historySize; h++) {
          System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
          for (int p = 0; p &lt; pointerCount; p++) {
              System.out.printf("  pointer %d: (%f,%f)",
                  ev.getPointerId(p), ev.getHistoricalX(p, h), ev.getHistoricalY(p, h));
          }
      }
      System.out.printf("At time %d:", ev.getEventTime());
      for (int p = 0; p &lt; pointerCount; p++) {
          System.out.printf("  pointer %d: (%f,%f)",
              ev.getPointerId(p), ev.getX(p), ev.getY(p));
      }
  }
  </code></pre></p>
 
  <h3>Device Types</h3>
  <p>
  The interpretation of the contents of a MotionEvent varies significantly depending
  on the source class of the device.
  </p><p>
  On pointing devices with source class {@link InputDevice#SOURCE_CLASS_POINTER}
  such as touch screens, the pointer coordinates specify absolute
  positions such as view X/Y coordinates.  Each complete gesture is represented
  by a sequence of motion events with actions that describe pointer state transitions
  and movements.  A gesture starts with a motion event with {@link #ACTION_DOWN}
  that provides the location of the first pointer down.  As each additional
  pointer that goes down or up, the framework will generate a motion event with
  {@link #ACTION_POINTER_DOWN} or {@link #ACTION_POINTER_UP} accordingly.
  Pointer movements are described by motion events with {@link #ACTION_MOVE}.
  Finally, a gesture end either when the final pointer goes up as represented
  by a motion event with {@link #ACTION_UP} or when gesture is canceled
  with {@link #ACTION_CANCEL}.
  </p><p>
  Some pointing devices such as mice may support vertical and/or horizontal scrolling.
  A scroll event is reported as a generic motion event with {@link #ACTION_SCROLL} that
  includes the relative scroll offset in the {@link #AXIS_VSCROLL} and
  {@link #AXIS_HSCROLL} axes.  See {@link #getAxisValue(int)} for information
  about retrieving these additional axes.
  </p><p>
  On trackball devices with source class {@link InputDevice#SOURCE_CLASS_TRACKBALL},
  the pointer coordinates specify relative movements as X/Y deltas.
  A trackball gesture consists of a sequence of movements described by motion
  events with {@link #ACTION_MOVE} interspersed with occasional {@link #ACTION_DOWN}
  or {@link #ACTION_UP} motion events when the trackball button is pressed or released.
  </p><p>
  On joystick devices with source class {@link InputDevice#SOURCE_CLASS_JOYSTICK},
  the pointer coordinates specify the absolute position of the joystick axes.
  The joystick axis values are normalized to a range of -1.0 to 1.0 where 0.0 corresponds
  to the center position.  More information about the set of available axes and the
  range of motion can be obtained using {@link InputDevice#getMotionRange}.
  Some common joystick axes are {@link #AXIS_X}, {@link #AXIS_Y},
  {@link #AXIS_HAT_X}, {@link #AXIS_HAT_Y}, {@link #AXIS_Z} and {@link #AXIS_RZ}.
  </p><p>
  Refer to {@link InputDevice} for more information about how different kinds of
  input devices and sources represent pointer coordinates.
  </p>
 
  <h3>Consistency Guarantees</h3>
  <p>
  Motion events are always delivered to views as a consistent stream of events.
  What constitutes a consistent stream varies depending on the type of device.
  For touch events, consistency implies that pointers go down one at a time,
  move around as a group and then go up one at a time or are canceled.
  </p><p>
  While the framework tries to deliver consistent streams of motion events to
  views, it cannot guarantee it.  Some events may be dropped or modified by
  containing views in the application before they are delivered thereby making
  the stream of events inconsistent.  Views should always be prepared to
  handle {@link #ACTION_CANCEL} and should tolerate anomalous
  situations such as receiving a new {@link #ACTION_DOWN} without first having
  received an {@link #ACTION_UP} for the prior gesture.
  </p>
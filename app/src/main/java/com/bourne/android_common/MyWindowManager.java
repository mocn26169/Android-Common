/*
版权所有（C）2006 Android开源项目
  
    根据Apache许可证2.0版（“许可证”）许可;
    您不得使用此文件，除非符合许可证。
    您可以获得许可证的副本
  
         http://www.apache.org/licenses/LICENSE-2.0
  
    除非适用法律要求或书面同意软件
    根据许可证分发的分发是“按原样”的基础，
    无明示或暗示的任何形式的担保或条件。
    请参阅有关权限的特定语言的许可证
    许可证下的限制。
 */

package android.view;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.app.Presentation;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.Objects;

/**
          应用程序用于与窗口管理器通信的界面。

      使用Context.getSystemService（Context.WINDOW_SERVICE）获取其中之一。

      每个窗口管理器实例绑定到一个特定的 Display。
      要获取不同显示的 WindowManager，请使用
       Context＃createDisplayContext}为此获取Context
      显示，然后使用 Context.getSystemService（Context.WINDOW_SERVICE）
      获取WindowManager。

      在另一个显示器上显示窗口的最简单的方法是创建一个
      Presentation。 演示文稿将自动获得
      WindowManager和Context。

      看 android.content.Context＃getSystemService
      看 android.content.Context＃WINDOW_SERVICE
 */
public interface WindowManager extends ViewManager {


    /** @hide */
    int DOCKED_INVALID = -1;
    /** @hide */
    int DOCKED_LEFT = 1;
    /** @hide */
    int DOCKED_TOP = 2;
    /** @hide */
    int DOCKED_RIGHT = 3;
    /** @hide */
    int DOCKED_BOTTOM = 4;

    /**
     尝试添加WindowManager.LayoutParams令牌无效的视图时抛出异常。
     */
    public static class BadTokenException extends RuntimeException {
        public BadTokenException() {
        }

        public BadTokenException(String name) {
            super(name);
        }
    }

    /**
     * 调用addView（View，ViewGroup.LayoutParams）到无法找到的辅助显示器时抛出的异常。
     * 有关辅助显示的详细信息，请参阅{@link android.app.Presentation}。
     */
    public static class InvalidDisplayException extends RuntimeException {
        public InvalidDisplayException() {
        }

        public InvalidDisplayException(String name) {
            super(name);
        }
    }

    /**
     返回此WindowManager实例将创建新窗口的显示。

     尽管此方法的名称，返回的显示不一定是系统的主要显示（请参阅DEFAULT_DISPLAY）。 返回的显示可以替代为此窗口管理器实例正在管理的辅助显示。 将其视为WindowManager实例默认使用的显示。

     要在不同的显示器上创建窗口，您需要为该显示器获取一个WindowManager。 （有关更多信息，请参阅WindowManager类文档。）

     返回

     该窗口管理器正在管理的显示。
     */
    public Display getDefaultDisplay();

    /**
     removeView（View）在返回之前立即调用给定视图层次结构的View.onDetachedFromWindow（）方法的特殊变体。 这不适用于正常应用; 正确使用它需要非常小心。

     参数
     view要删除的视图。
     */
    public void removeViewImmediate(View view);

    /**
     * 用于从焦点窗口异步请求键盘快捷键。
     *
     * @hide
     */
    public interface KeyboardShortcutsReceiver {
        /**
         当对焦窗口键盘快捷键准备好显示时使用回调。
                  
         参数 result ：
         要显示的键盘快捷键。
         */
        void onKeyboardShortcutsReceived(List<KeyboardShortcutGroup> result);
    }

    /**
     提供全屏截图的消息
     * @hide
     */
    final int TAKE_SCREENSHOT_FULLSCREEN = 1;

    /**
     取消所选区域截图的消息。
     * @hide
     */
    final int TAKE_SCREENSHOT_SELECTED_REGION = 2;

    /**
     * @hide
     */
    public static final String PARCEL_KEY_SHORTCUTS_ARRAY = "shortcuts_array";

    /**
     *要求异步检索键盘快捷键。

     参数receiver：
     当结果准备就绪时要触发的回调。
     *
     * @hide
     */
    public void requestAppKeyboardShortcuts(final KeyboardShortcutsReceiver receiver, int deviceId);

    public static class LayoutParams extends ViewGroup.LayoutParams implements Parcelable {
        /**
         此窗口的X位置。 使用默认重力，它被忽略。
         使用{@link重力＃LEFT}或{@link重力＃START}或{@link重力＃RIGHT}或
         {@link Gravity＃END}它提供了从给定边缘的偏移量。
         */
        @ViewDebug.ExportedProperty
        public int x;

        /**
         * Y position for this window.  With the default gravity it is ignored.
         * When using {@link Gravity#TOP} or {@link Gravity#BOTTOM} it provides
         * an offset from the given edge.
         * *这个窗口的Y位置。 使用默认重力，它被忽略。
                   *使用{@link Gravity＃TOP}或{@link Gravity＃BOTTOM}时提供
                   *与给定边缘的偏移。
         */
        @ViewDebug.ExportedProperty
        public int y;

        /**
         指示将水平分配给与这些LayoutParams相关联的视图的多少空间。 如果视图不应该被拉伸，则指定0。 否则，在重量大于0的所有视图中，额外的像素将被评估。
         */
        @ViewDebug.ExportedProperty
        public float horizontalWeight;

        /**
         指示垂直分配给与这些LayoutParams相关联的视图的额外空间量。 如果视图不应该被拉伸，则指定0。 否则，在重量大于0的所有视图中，额外的像素将被评估。
         */
        @ViewDebug.ExportedProperty
        public float verticalWeight;

        /**
         窗口的一般类型。窗口类型有三大类：
                  
                        应用程序窗口（从FIRST_APPLICATION_WINDOW至
                     LAST_APPLICATION_WINDOW）是正常的顶级应用程序
                     视窗。对于这些类型的窗口，token必须是
                     设置为他们是其中一部分的活动的标志（这将会
                     如果token为空，通常可以为您完成）。

                       子窗口（从FIRST_SUB_WINDOW}至
                      #LAST_SUB_WINDOW}与另一个顶级别相关联
                     窗口。对于这些类型的窗口，token必须是
                     它附加到的窗口的标记。

                     系统窗口（从FIRST_SYSTEM_WINDOW至
                      LAST_SYSTEM_WINDOW）是特殊类型的窗口
                     由系统用于特定目的。他们不应该正常
                     由应用程序使用，需要特殊许可
                     使用它们。
                   
         *
         * @see #TYPE_BASE_APPLICATION
         * @see #TYPE_APPLICATION
         * @see #TYPE_APPLICATION_STARTING
         * @see #TYPE_DRAWN_APPLICATION
         * @see #TYPE_APPLICATION_PANEL
         * @see #TYPE_APPLICATION_MEDIA
         * @see #TYPE_APPLICATION_SUB_PANEL
         * @see #TYPE_APPLICATION_ABOVE_SUB_PANEL
         * @see #TYPE_APPLICATION_ATTACHED_DIALOG
         * @see #TYPE_STATUS_BAR
         * @see #TYPE_SEARCH_BAR
         * @see #TYPE_PHONE
         * @see #TYPE_SYSTEM_ALERT
         * @see #TYPE_TOAST
         * @see #TYPE_SYSTEM_OVERLAY
         * @see #TYPE_PRIORITY_PHONE
         * @see #TYPE_STATUS_BAR_PANEL
         * @see #TYPE_SYSTEM_DIALOG
         * @see #TYPE_KEYGUARD_DIALOG
         * @see #TYPE_SYSTEM_ERROR
         * @see #TYPE_INPUT_METHOD
         * @see #TYPE_INPUT_METHOD_DIALOG
         */
        @ViewDebug.ExportedProperty(mapping = {
                @ViewDebug.IntToString(from = TYPE_BASE_APPLICATION, to = "TYPE_BASE_APPLICATION"),
                @ViewDebug.IntToString(from = TYPE_APPLICATION, to = "TYPE_APPLICATION"),
                @ViewDebug.IntToString(from = TYPE_APPLICATION_STARTING, to = "TYPE_APPLICATION_STARTING"),
                @ViewDebug.IntToString(from = TYPE_DRAWN_APPLICATION, to = "TYPE_DRAWN_APPLICATION"),
                @ViewDebug.IntToString(from = TYPE_APPLICATION_PANEL, to = "TYPE_APPLICATION_PANEL"),
                @ViewDebug.IntToString(from = TYPE_APPLICATION_MEDIA, to = "TYPE_APPLICATION_MEDIA"),
                @ViewDebug.IntToString(from = TYPE_APPLICATION_SUB_PANEL, to = "TYPE_APPLICATION_SUB_PANEL"),
                @ViewDebug.IntToString(from = TYPE_APPLICATION_ABOVE_SUB_PANEL, to = "TYPE_APPLICATION_ABOVE_SUB_PANEL"),
                @ViewDebug.IntToString(from = TYPE_APPLICATION_ATTACHED_DIALOG, to = "TYPE_APPLICATION_ATTACHED_DIALOG"),
                @ViewDebug.IntToString(from = TYPE_APPLICATION_MEDIA_OVERLAY, to = "TYPE_APPLICATION_MEDIA_OVERLAY"),
                @ViewDebug.IntToString(from = TYPE_STATUS_BAR, to = "TYPE_STATUS_BAR"),
                @ViewDebug.IntToString(from = TYPE_SEARCH_BAR, to = "TYPE_SEARCH_BAR"),
                @ViewDebug.IntToString(from = TYPE_PHONE, to = "TYPE_PHONE"),
                @ViewDebug.IntToString(from = TYPE_SYSTEM_ALERT, to = "TYPE_SYSTEM_ALERT"),
                @ViewDebug.IntToString(from = TYPE_TOAST, to = "TYPE_TOAST"),
                @ViewDebug.IntToString(from = TYPE_SYSTEM_OVERLAY, to = "TYPE_SYSTEM_OVERLAY"),
                @ViewDebug.IntToString(from = TYPE_PRIORITY_PHONE, to = "TYPE_PRIORITY_PHONE"),
                @ViewDebug.IntToString(from = TYPE_SYSTEM_DIALOG, to = "TYPE_SYSTEM_DIALOG"),
                @ViewDebug.IntToString(from = TYPE_KEYGUARD_DIALOG, to = "TYPE_KEYGUARD_DIALOG"),
                @ViewDebug.IntToString(from = TYPE_SYSTEM_ERROR, to = "TYPE_SYSTEM_ERROR"),
                @ViewDebug.IntToString(from = TYPE_INPUT_METHOD, to = "TYPE_INPUT_METHOD"),
                @ViewDebug.IntToString(from = TYPE_INPUT_METHOD_DIALOG, to = "TYPE_INPUT_METHOD_DIALOG"),
                @ViewDebug.IntToString(from = TYPE_WALLPAPER, to = "TYPE_WALLPAPER"),
                @ViewDebug.IntToString(from = TYPE_STATUS_BAR_PANEL, to = "TYPE_STATUS_BAR_PANEL"),
                @ViewDebug.IntToString(from = TYPE_SECURE_SYSTEM_OVERLAY, to = "TYPE_SECURE_SYSTEM_OVERLAY"),
                @ViewDebug.IntToString(from = TYPE_DRAG, to = "TYPE_DRAG"),
                @ViewDebug.IntToString(from = TYPE_STATUS_BAR_SUB_PANEL, to = "TYPE_STATUS_BAR_SUB_PANEL"),
                @ViewDebug.IntToString(from = TYPE_POINTER, to = "TYPE_POINTER"),
                @ViewDebug.IntToString(from = TYPE_NAVIGATION_BAR, to = "TYPE_NAVIGATION_BAR"),
                @ViewDebug.IntToString(from = TYPE_VOLUME_OVERLAY, to = "TYPE_VOLUME_OVERLAY"),
                @ViewDebug.IntToString(from = TYPE_BOOT_PROGRESS, to = "TYPE_BOOT_PROGRESS"),
                @ViewDebug.IntToString(from = TYPE_INPUT_CONSUMER, to = "TYPE_INPUT_CONSUMER"),
                @ViewDebug.IntToString(from = TYPE_DREAM, to = "TYPE_DREAM"),
                @ViewDebug.IntToString(from = TYPE_NAVIGATION_BAR_PANEL, to = "TYPE_NAVIGATION_BAR_PANEL"),
                @ViewDebug.IntToString(from = TYPE_DISPLAY_OVERLAY, to = "TYPE_DISPLAY_OVERLAY"),
                @ViewDebug.IntToString(from = TYPE_MAGNIFICATION_OVERLAY, to = "TYPE_MAGNIFICATION_OVERLAY"),
                @ViewDebug.IntToString(from = TYPE_PRIVATE_PRESENTATION, to = "TYPE_PRIVATE_PRESENTATION"),
                @ViewDebug.IntToString(from = TYPE_VOICE_INTERACTION, to = "TYPE_VOICE_INTERACTION"),
                @ViewDebug.IntToString(from = TYPE_VOICE_INTERACTION_STARTING, to = "TYPE_VOICE_INTERACTION_STARTING"),
                @ViewDebug.IntToString(from = TYPE_DOCK_DIVIDER, to = "TYPE_DOCK_DIVIDER"),
                @ViewDebug.IntToString(from = TYPE_QS_DIALOG, to = "TYPE_QS_DIALOG"),
                @ViewDebug.IntToString(from = TYPE_SCREENSHOT, to = "TYPE_SCREENSHOT")
        })
        public int type;

        /**
         * 开始表示正常应用程序窗口的窗口类型。
         */
        public static final int FIRST_APPLICATION_WINDOW = 1;

        /**
         窗口类型：作为整个应用程序的“基本”窗口的应用程序窗口; 所有其他应用程序窗口都将显示在其上。 在多用户系统中，仅显示拥有用户的窗口。
         */
        public static final int TYPE_BASE_APPLICATION   = 1;

        /**
         窗口类型：正常应用程序窗口。 token必须是一个活动标记，用于标识该窗口属于哪个人。 在多用户系统中，仅显示拥有用户的窗口。
         */
        public static final int TYPE_APPLICATION        = 2;

        /**
         窗口类型：应用程序启动时显示的特殊应用程序窗口。
         不适用于应用程序本身; 这被系统用来显示内容，直到应用程序显示自己的窗口。
         在多用户系统中显示所有用户的窗口。
         */
        public static final int TYPE_APPLICATION_STARTING = 3;

        /**
         窗口类型：TYPE APPLICATION的变体，确保窗口管理器等待在显示应用程序之前绘制该窗口。
         在多用户系统中，仅显示拥有用户的窗口。
         */
        public static final int TYPE_DRAWN_APPLICATION = 4;

        /**
         * 应用程序窗口类型的结束。
         */
        public static final int LAST_APPLICATION_WINDOW = 99;

        /**
         启动子窗口的类型。
         这些窗口的token必须设置为它们附加到的窗口。
         这些类型的窗口以Z顺序保持在其附加的窗口旁边，并且它们的坐标空间相对于它们附接的窗口。
         */
        public static final int FIRST_SUB_WINDOW = 1000;

        /**
         窗口类型：应用程序窗口顶部的面板。 这些窗口出现在其附近窗口的顶部。
         */
        public static final int TYPE_APPLICATION_PANEL = FIRST_SUB_WINDOW;

        /**
         窗口类型：显示媒体的窗口（如视频）。 这些窗口显示在其附近的窗口后面。
         */
        public static final int TYPE_APPLICATION_MEDIA = FIRST_SUB_WINDOW + 1;

        /**
         窗口类型：应用程序窗口顶部的子面板。 这些窗口显示在其附近的窗口和任何TYPE_APPLICATION_PANEL面板上。
         */
        public static final int TYPE_APPLICATION_SUB_PANEL = FIRST_SUB_WINDOW + 2;

        /**
         * 窗口类型：像TYPE_APPLICATION_PANEL，但窗口的布局与顶级窗口的布局不同，而不是其容器的小孩。
         */
        public static final int TYPE_APPLICATION_ATTACHED_DIALOG = FIRST_SUB_WINDOW + 3;

        /**
         窗口类型：在媒体窗口顶部显示覆盖的窗口。
         这些窗口显示在TYPE_APPLICATION_MEDIA和应用程序窗口之间。
         它们应该是半透明的，以便有用。 这是一个很丑的黑客：
         * @hide
         */
        public static final int TYPE_APPLICATION_MEDIA_OVERLAY  = FIRST_SUB_WINDOW + 4;

        /**
         窗口类型：应用程序窗口顶部的上一个子面板，它是子窗口窗口。 这些窗口显示在所附窗口和任何TYPE_APPLICATION_SUB_PANEL面板的顶部。
         * @hide
         */
        public static final int TYPE_APPLICATION_ABOVE_SUB_PANEL = FIRST_SUB_WINDOW + 5;

        /**
         * 子窗口类型的结束。
         */
        public static final int LAST_SUB_WINDOW = 1999;

        /**
          启动系统特定的窗口类型。 这些通常不是由应用程序创建的。
         */
        public static final int FIRST_SYSTEM_WINDOW     = 2000;

        /**
         窗口类型：状态栏。 只能有一个状态栏窗口; 它被放置在屏幕的顶部，所有其他窗口都向下移动，所以它们在它的下方。 在多用户系统中显示所有用户的窗口。
         */
        public static final int TYPE_STATUS_BAR         = FIRST_SYSTEM_WINDOW;

        /**
         窗口类型：搜索栏。 只能有一个搜索栏窗口; 它被放置在屏幕的顶部。 在多用户系统中显示所有用户的窗口。
         */
        public static final int TYPE_SEARCH_BAR         = FIRST_SYSTEM_WINDOW+1;

        /**
         窗口类型：手机。
         这些是提供用户与电话（特别是来电）的交互的非应用程序窗口。
         这些窗口通常位于所有应用程序之上，但位于状态栏的后面。
         在多用户系统中显示所有用户的窗口。
         */
        public static final int TYPE_PHONE              = FIRST_SYSTEM_WINDOW+2;

        /**
         窗口类型：系统窗口，如低功率警报。 这些窗口始终位于应用程序窗口之上。 在多用户系统中只显示所有用户的窗口。
         */
        public static final int TYPE_SYSTEM_ALERT       = FIRST_SYSTEM_WINDOW+3;

        /**
         窗口类型：键盘保护窗口。 在多用户系统中显示所有用户的窗口。
         * @removed
         */
        public static final int TYPE_KEYGUARD           = FIRST_SYSTEM_WINDOW+4;

        /**
         窗口类型：瞬态通知。 在多用户系统中，仅显示拥有用户的窗口。
         */
        public static final int TYPE_TOAST              = FIRST_SYSTEM_WINDOW+5;

        /**
         窗口类型：系统覆盖窗口，需要显示在其他的顶部。 这些窗口不能采取输入焦点，否则会干扰键盘保护。 在多用户系统中，仅显示拥有用户的窗口。
         */
        public static final int TYPE_SYSTEM_OVERLAY     = FIRST_SYSTEM_WINDOW+6;

        /**
         窗口类型：优先手机UI，即使键盘保持活动，也需要显示。 这些窗口不能采取输入焦点，否则会干扰键盘保护。 在多用户系统中显示所有用户的窗口。
         */
        public static final int TYPE_PRIORITY_PHONE     = FIRST_SYSTEM_WINDOW+7;

        /**
         窗口类型：从状态栏滑出的面板在多用户系统中显示所有用户的窗口。
         */
        public static final int TYPE_SYSTEM_DIALOG      = FIRST_SYSTEM_WINDOW+8;

        /**
         窗口类型：键盘显示的对话框在多用户系统中显示所有用户的窗口。
         */
        public static final int TYPE_KEYGUARD_DIALOG    = FIRST_SYSTEM_WINDOW+9;

        /**
         窗口类型：内部系统错误窗口，出现在他们可以做的一切之上。
           在多用户系统中，仅显示拥有用户的窗口。
         */
        public static final int TYPE_SYSTEM_ERROR       = FIRST_SYSTEM_WINDOW+10;

        /**
         窗口类型：内部输入法窗口，它们出现在普通UI之上。
         应用程序窗口可以调整大小或平移，以便在显示此窗口时保持输入焦点可见。
         在多用户系统中，仅显示拥有用户的窗口。
         */
        public static final int TYPE_INPUT_METHOD       = FIRST_SYSTEM_WINDOW+11;

        /**
         窗口类型：内部输入法对话框窗口，显示在当前输入法窗口上方。 在多用户系统中只显示拥有用户的窗口。
         */
        public static final int TYPE_INPUT_METHOD_DIALOG= FIRST_SYSTEM_WINDOW+12;

        /**
         窗口类型：壁纸窗口，放置在任何希望坐在壁纸顶部的窗口之后。 在多用户系统中，仅显示拥有用户的窗口。
         */
        public static final int TYPE_WALLPAPER          = FIRST_SYSTEM_WINDOW+13;

        /**
         窗口类型：面板，从在状态栏在多用户系统中滑出显示了所有用户的窗口。
         */
        public static final int TYPE_STATUS_BAR_PANEL   = FIRST_SYSTEM_WINDOW+14;

        /**
         窗口类型：安全系统覆盖窗口，需要显示在其他的顶部。 这些窗口不能采取输入焦点，否则会干扰键盘保护。

         这与{TYPE_SYSTEM_OVERLAY完全相同，只有系统本身才允许创建这些叠加层。 应用程序无法获取创建安全系统覆盖的权限。

         在多用户系统中，仅显示拥有用户的窗口。
         * @hide
         */
        public static final int TYPE_SECURE_SYSTEM_OVERLAY = FIRST_SYSTEM_WINDOW+15;

        /**
         窗口类型：拖放伪窗口。 只有一个拖动层（最多），它被放置在所有其他窗口的顶部。 在多用户系统中只显示拥有用户的窗口。
         * @hide
         */
        public static final int TYPE_DRAG               = FIRST_SYSTEM_WINDOW+16;

        /**
         窗口类型：从状态栏下方滑出的面板在多用户系统中显示所有用户的窗口。
         * @hide
         */
        public static final int TYPE_STATUS_BAR_SUB_PANEL = FIRST_SYSTEM_WINDOW+17;

        /**
         窗口类型：（鼠标）指针在多用户系统中显示所有用户的窗口。
         * @hide
         */
        public static final int TYPE_POINTER = FIRST_SYSTEM_WINDOW+18;

        /**
         窗口类型：导航栏（与状态栏不同）在多用户系统中，在所有用户的窗口中都会显示。
         * @hide
         */
        public static final int TYPE_NAVIGATION_BAR = FIRST_SYSTEM_WINDOW+19;

        /**
         窗口类型：当用户更改系统卷时显示的音量级别覆盖/对话框。 在多用户系统中显示所有用户的窗口。
         * @hide
         */
        public static final int TYPE_VOLUME_OVERLAY = FIRST_SYSTEM_WINDOW+20;

        /**
         窗口类型：引导进度对话框，位于世界各地。 在多用户系统中显示所有用户的窗口。
         * @hide
         */
        public static final int TYPE_BOOT_PROGRESS = FIRST_SYSTEM_WINDOW+21;

        /**
         窗口类型在系统UI栏被隐藏时消耗输入事件。 在多用户系统中显示所有用户的窗口。
         * @hide
         */
        public static final int TYPE_INPUT_CONSUMER = FIRST_SYSTEM_WINDOW+22;

        /**
         窗口类型：Dreams（屏幕保护程序）窗口，就在键盘上方。
         在多用户系统中，仅显示拥有用户的窗口。
         * @hide
         */
        public static final int TYPE_DREAM = FIRST_SYSTEM_WINDOW+23;

        /**
         窗口类型：导航栏面板（导航栏与状态栏不同）在多用户系统中，所有用户的窗口都将显示。
         * @hide
         */
        public static final int TYPE_NAVIGATION_BAR_PANEL = FIRST_SYSTEM_WINDOW+24;

        /**
         *窗口类型：显示覆盖窗口。 用于模拟二次显示设备。
           在多用户系统中显示所有用户的窗口。
         * @hide
         */
        public static final int TYPE_DISPLAY_OVERLAY = FIRST_SYSTEM_WINDOW+26;

        /**
         窗口类型：放大倍数窗口。 用于突出显示器的放大部分，当启用可访问性缩放时。 在多用户系统中显示所有用户的窗口。
         * @hide
         */
        public static final int TYPE_MAGNIFICATION_OVERLAY = FIRST_SYSTEM_WINDOW+27;

        /**
         窗口类型：keyguard scrim窗口。 keyguard的显示需要重新启动。 在多用户系统中显示所有用户的窗口。
         * @hide
         */
        public static final int TYPE_KEYGUARD_SCRIM           = FIRST_SYSTEM_WINDOW+29;

        /**
         窗口类型：用于在私有虚拟显示器上显示的窗口。
         */
        public static final int TYPE_PRIVATE_PRESENTATION = FIRST_SYSTEM_WINDOW+30;

        /**
         * 窗口类型：Windows中的语音交互层。
         * @hide
         */
        public static final int TYPE_VOICE_INTERACTION = FIRST_SYSTEM_WINDOW+31;

        /**
         窗口类型：Windows仅由连接的{@link android.accessibilityservice.AccessibilityService}覆盖，用于拦截用户交互，而无需更改辅助功能服务可以内省的窗口。
         特别地，可访问性服务可以仅仅检视目标用户可以与哪些窗口进行交互，这些窗口可以触摸这些窗口，或者可以键入这些窗口。
         例如，如果存在可触摸屏幕的全屏辅助功能重叠，则即使它们被可触摸窗口覆盖，其下方的窗口也将由辅助功能服务内省。
         */
        public static final int TYPE_ACCESSIBILITY_OVERLAY = FIRST_SYSTEM_WINDOW+32;

        /**
         * 窗口类型：语音交互层的启动窗口。
         * @hide
         */
        public static final int TYPE_VOICE_INTERACTION_STARTING = FIRST_SYSTEM_WINDOW+33;

        /**
         用于显示用于调整停靠堆栈大小的句柄的窗口。 该窗口由系统进程所有。
         * @hide
         */
        public static final int TYPE_DOCK_DIVIDER = FIRST_SYSTEM_WINDOW+34;

        /**
         *窗口类型：像TYPE_APPLICATION_ATTACHED_DIALOG，但由快速设置瓷砖使用。
         * @hide
         */
        public static final int TYPE_QS_DIALOG = FIRST_SYSTEM_WINDOW+35;

        /**
         窗口类型：与TYPE_DREAM共享类似的特征。 该图层保留用于截图区域选择。 这些窗口不能采取输入焦点。
         * @hide
         */
        public static final int TYPE_SCREENSHOT = FIRST_SYSTEM_WINDOW + 36;

        /**
         * 系统窗口类型结束。
         */
        public static final int LAST_SYSTEM_WINDOW      = 2999;

        /** @deprecated这被忽略，这个值在需要时自动设置。 */
        @Deprecated
        public static final int MEMORY_TYPE_NORMAL = 0;
        /** @deprecated这被忽略，这个值在需要时自动设置。 */
        @Deprecated
        public static final int MEMORY_TYPE_HARDWARE = 1;
        /** @deprecated这被忽略，这个值在需要时自动设置。 */
        @Deprecated
        public static final int MEMORY_TYPE_GPU = 2;
        /** @deprecated这被忽略，这个值在需要时自动设置。 */
        @Deprecated
        public static final int MEMORY_TYPE_PUSH_BUFFERS = 3;

        /**
         * @deprecated这被忽略
         */
        @Deprecated
        public int memoryType;

        /** Window flag: as long as this window is visible to the user, allow
         *  the lock screen to activate while the screen is on.
         *  This can be used independently, or in combination with
         *  {@link #FLAG_KEEP_SCREEN_ON} and/or {@link #FLAG_SHOW_WHEN_LOCKED} */
        public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON     = 0x00000001;

        /** Window flag: everything behind this window will be dimmed.
         *  Use {@link #dimAmount} to control the amount of dim. */
        public static final int FLAG_DIM_BEHIND        = 0x00000002;

        /** Window flag: blur everything behind this window.
         * @deprecated Blurring is no longer supported. */
        @Deprecated
        public static final int FLAG_BLUR_BEHIND        = 0x00000004;

        /** Window flag: this window won't ever get key input focus, so the
         * user can not send key or other button events to it.  Those will
         * instead go to whatever focusable window is behind it.  This flag
         * will also enable {@link #FLAG_NOT_TOUCH_MODAL} whether or not that
         * is explicitly set.
         *
         * <p>Setting this flag also implies that the window will not need to
         * interact with
         * a soft input method, so it will be Z-ordered and positioned
         * independently of any active input method (typically this means it
         * gets Z-ordered on top of the input method, so it can use the full
         * screen for its content and cover the input method if needed.  You
         * can use {@link #FLAG_ALT_FOCUSABLE_IM} to modify this behavior. */
        public static final int FLAG_NOT_FOCUSABLE      = 0x00000008;

        /** Window flag: this window can never receive touch events. */
        public static final int FLAG_NOT_TOUCHABLE      = 0x00000010;

        /** Window flag: even when this window is focusable (its
         * {@link #FLAG_NOT_FOCUSABLE} is not set), allow any pointer events
         * outside of the window to be sent to the windows behind it.  Otherwise
         * it will consume all pointer events itself, regardless of whether they
         * are inside of the window. */
        public static final int FLAG_NOT_TOUCH_MODAL    = 0x00000020;

        /** Window flag: when set, if the device is asleep when the touch
         * screen is pressed, you will receive this first touch event.  Usually
         * the first touch event is consumed by the system since the user can
         * not see what they are pressing on.
         *
         * @deprecated This flag has no effect.
         */
        @Deprecated
        public static final int FLAG_TOUCHABLE_WHEN_WAKING = 0x00000040;

        /** Window flag: as long as this window is visible to the user, keep
         *  the device's screen turned on and bright. */
        public static final int FLAG_KEEP_SCREEN_ON     = 0x00000080;

        /** Window flag: place the window within the entire screen, ignoring
         *  decorations around the border (such as the status bar).  The
         *  window must correctly position its contents to take the screen
         *  decoration into account.  This flag is normally set for you
         *  by Window as described in {@link Window#setFlags}. */
        public static final int FLAG_LAYOUT_IN_SCREEN   = 0x00000100;

        /** Window flag: allow window to extend outside of the screen. */
        public static final int FLAG_LAYOUT_NO_LIMITS   = 0x00000200;

        /**
         * Window flag: hide all screen decorations (such as the status bar) while
         * this window is displayed.  This allows the window to use the entire
         * display space for itself -- the status bar will be hidden when
         * an app window with this flag set is on the top layer. A fullscreen window
         * will ignore a value of {@link #SOFT_INPUT_ADJUST_RESIZE} for the window's
         * {@link #softInputMode} field; the window will stay fullscreen
         * and will not resize.
         *
         * <p>This flag can be controlled in your theme through the
         * {@link android.R.attr#windowFullscreen} attribute; this attribute
         * is automatically set for you in the standard fullscreen themes
         * such as {@link android.R.style#Theme_NoTitleBar_Fullscreen},
         * {@link android.R.style#Theme_Black_NoTitleBar_Fullscreen},
         * {@link android.R.style#Theme_Light_NoTitleBar_Fullscreen},
         * {@link android.R.style#Theme_Holo_NoActionBar_Fullscreen},
         * {@link android.R.style#Theme_Holo_Light_NoActionBar_Fullscreen},
         * {@link android.R.style#Theme_DeviceDefault_NoActionBar_Fullscreen}, and
         * {@link android.R.style#Theme_DeviceDefault_Light_NoActionBar_Fullscreen}.</p>
         */
        public static final int FLAG_FULLSCREEN      = 0x00000400;

        /** Window flag: override {@link #FLAG_FULLSCREEN} and force the
         *  screen decorations (such as the status bar) to be shown. */
        public static final int FLAG_FORCE_NOT_FULLSCREEN   = 0x00000800;

        /** Window flag: turn on dithering when compositing this window to
         *  the screen.
         * @deprecated This flag is no longer used. */
        @Deprecated
        public static final int FLAG_DITHER             = 0x00001000;

        /** Window flag: treat the content of the window as secure, preventing
         * it from appearing in screenshots or from being viewed on non-secure
         * displays.
         *
         * <p>See {@link android.view.Display#FLAG_SECURE} for more details about
         * secure surfaces and secure displays.
         */
        public static final int FLAG_SECURE             = 0x00002000;

        /** Window flag: a special mode where the layout parameters are used
         * to perform scaling of the surface when it is composited to the
         * screen. */
        public static final int FLAG_SCALED             = 0x00004000;

        /** Window flag: intended for windows that will often be used when the user is
         * holding the screen against their face, it will aggressively filter the event
         * stream to prevent unintended presses in this situation that may not be
         * desired for a particular window, when such an event stream is detected, the
         * application will receive a CANCEL motion event to indicate this so applications
         * can handle this accordingly by taking no action on the event
         * until the finger is released. */
        public static final int FLAG_IGNORE_CHEEK_PRESSES    = 0x00008000;

        /** Window flag: a special option only for use in combination with
         * {@link #FLAG_LAYOUT_IN_SCREEN}.  When requesting layout in the
         * screen your window may appear on top of or behind screen decorations
         * such as the status bar.  By also including this flag, the window
         * manager will report the inset rectangle needed to ensure your
         * content is not covered by screen decorations.  This flag is normally
         * set for you by Window as described in {@link Window#setFlags}.*/
        public static final int FLAG_LAYOUT_INSET_DECOR = 0x00010000;

        /** Window flag: invert the state of {@link #FLAG_NOT_FOCUSABLE} with
         * respect to how this window interacts with the current method.  That
         * is, if FLAG_NOT_FOCUSABLE is set and this flag is set, then the
         * window will behave as if it needs to interact with the input method
         * and thus be placed behind/away from it; if FLAG_NOT_FOCUSABLE is
         * not set and this flag is set, then the window will behave as if it
         * doesn't need to interact with the input method and can be placed
         * to use more space and cover the input method.
         */
        public static final int FLAG_ALT_FOCUSABLE_IM = 0x00020000;

        /** Window flag: if you have set {@link #FLAG_NOT_TOUCH_MODAL}, you
         * can set this flag to receive a single special MotionEvent with
         * the action
         * {@link MotionEvent#ACTION_OUTSIDE MotionEvent.ACTION_OUTSIDE} for
         * touches that occur outside of your window.  Note that you will not
         * receive the full down/move/up gesture, only the location of the
         * first down as an ACTION_OUTSIDE.
         */
        public static final int FLAG_WATCH_OUTSIDE_TOUCH = 0x00040000;

        /** Window flag: special flag to let windows be shown when the screen
         * is locked. This will let application windows take precedence over
         * key guard or any other lock screens. Can be used with
         * {@link #FLAG_KEEP_SCREEN_ON} to turn screen on and display windows
         * directly before showing the key guard window.  Can be used with
         * {@link #FLAG_DISMISS_KEYGUARD} to automatically fully dismisss
         * non-secure keyguards.  This flag only applies to the top-most
         * full-screen window.
         */
        public static final int FLAG_SHOW_WHEN_LOCKED = 0x00080000;

        /** Window flag: ask that the system wallpaper be shown behind
         * your window.  The window surface must be translucent to be able
         * to actually see the wallpaper behind it; this flag just ensures
         * that the wallpaper surface will be there if this window actually
         * has translucent regions.
         *
         * <p>This flag can be controlled in your theme through the
         * {@link android.R.attr#windowShowWallpaper} attribute; this attribute
         * is automatically set for you in the standard wallpaper themes
         * such as {@link android.R.style#Theme_Wallpaper},
         * {@link android.R.style#Theme_Wallpaper_NoTitleBar},
         * {@link android.R.style#Theme_Wallpaper_NoTitleBar_Fullscreen},
         * {@link android.R.style#Theme_Holo_Wallpaper},
         * {@link android.R.style#Theme_Holo_Wallpaper_NoTitleBar},
         * {@link android.R.style#Theme_DeviceDefault_Wallpaper}, and
         * {@link android.R.style#Theme_DeviceDefault_Wallpaper_NoTitleBar}.</p>
         */
        public static final int FLAG_SHOW_WALLPAPER = 0x00100000;

        /** Window flag: when set as a window is being added or made
         * visible, once the window has been shown then the system will
         * poke the power manager's user activity (as if the user had woken
         * up the device) to turn the screen on. */
        public static final int FLAG_TURN_SCREEN_ON = 0x00200000;

        /** Window flag: when set the window will cause the keyguard to
         * be dismissed, only if it is not a secure lock keyguard.  Because such
         * a keyguard is not needed for security, it will never re-appear if
         * the user navigates to another window (in contrast to
         * {@link #FLAG_SHOW_WHEN_LOCKED}, which will only temporarily
         * hide both secure and non-secure keyguards but ensure they reappear
         * when the user moves to another UI that doesn't hide them).
         * If the keyguard is currently active and is secure (requires an
         * unlock pattern) than the user will still need to confirm it before
         * seeing this window, unless {@link #FLAG_SHOW_WHEN_LOCKED} has
         * also been set.
         */
        public static final int FLAG_DISMISS_KEYGUARD = 0x00400000;

        /** Window flag: when set the window will accept for touch events
         * outside of its bounds to be sent to other windows that also
         * support split touch.  When this flag is not set, the first pointer
         * that goes down determines the window to which all subsequent touches
         * go until all pointers go up.  When this flag is set, each pointer
         * (not necessarily the first) that goes down determines the window
         * to which all subsequent touches of that pointer will go until that
         * pointer goes up thereby enabling touches with multiple pointers
         * to be split across multiple windows.
         */
        public static final int FLAG_SPLIT_TOUCH = 0x00800000;

        /**
         * <p>Indicates whether this window should be hardware accelerated.
         * Requesting hardware acceleration does not guarantee it will happen.</p>
         *
         * <p>This flag can be controlled programmatically <em>only</em> to enable
         * hardware acceleration. To enable hardware acceleration for a given
         * window programmatically, do the following:</p>
         *
         * <pre>
         * Window w = activity.getWindow(); // in Activity's onCreate() for instance
         * w.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
         *         WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
         * </pre>
         *
         * <p>It is important to remember that this flag <strong>must</strong>
         * be set before setting the content view of your activity or dialog.</p>
         *
         * <p>This flag cannot be used to disable hardware acceleration after it
         * was enabled in your manifest using
         * {@link android.R.attr#hardwareAccelerated}. If you need to selectively
         * and programmatically disable hardware acceleration (for automated testing
         * for instance), make sure it is turned off in your manifest and enable it
         * on your activity or dialog when you need it instead, using the method
         * described above.</p>
         *
         * <p>This flag is automatically set by the system if the
         * {@link android.R.attr#hardwareAccelerated android:hardwareAccelerated}
         * XML attribute is set to true on an activity or on the application.</p>
         */
        public static final int FLAG_HARDWARE_ACCELERATED = 0x01000000;

        /**
         * Window flag: allow window contents to extend in to the screen's
         * overscan area, if there is one.  The window should still correctly
         * position its contents to take the overscan area into account.
         *
         * <p>This flag can be controlled in your theme through the
         * {@link android.R.attr#windowOverscan} attribute; this attribute
         * is automatically set for you in the standard overscan themes
         * such as
         * {@link android.R.style#Theme_Holo_NoActionBar_Overscan},
         * {@link android.R.style#Theme_Holo_Light_NoActionBar_Overscan},
         * {@link android.R.style#Theme_DeviceDefault_NoActionBar_Overscan}, and
         * {@link android.R.style#Theme_DeviceDefault_Light_NoActionBar_Overscan}.</p>
         *
         * <p>When this flag is enabled for a window, its normal content may be obscured
         * to some degree by the overscan region of the display.  To ensure key parts of
         * that content are visible to the user, you can use
         * {@link View#setFitsSystemWindows(boolean) View.setFitsSystemWindows(boolean)}
         * to set the point in the view hierarchy where the appropriate offsets should
         * be applied.  (This can be done either by directly calling this function, using
         * the {@link android.R.attr#fitsSystemWindows} attribute in your view hierarchy,
         * or implementing you own {@link View#fitSystemWindows(android.graphics.Rect)
         * View.fitSystemWindows(Rect)} method).</p>
         *
         * <p>This mechanism for positioning content elements is identical to its equivalent
         * use with layout and {@link View#setSystemUiVisibility(int)
         * View.setSystemUiVisibility(int)}; here is an example layout that will correctly
         * position its UI elements with this overscan flag is set:</p>
         *
         * {@sample development/samples/ApiDemos/res/layout/overscan_activity.xml complete}
         */
        public static final int FLAG_LAYOUT_IN_OVERSCAN = 0x02000000;

        /**
         * Window flag: request a translucent status bar with minimal system-provided
         * background protection.
         *
         * <p>This flag can be controlled in your theme through the
         * {@link android.R.attr#windowTranslucentStatus} attribute; this attribute
         * is automatically set for you in the standard translucent decor themes
         * such as
         * {@link android.R.style#Theme_Holo_NoActionBar_TranslucentDecor},
         * {@link android.R.style#Theme_Holo_Light_NoActionBar_TranslucentDecor},
         * {@link android.R.style#Theme_DeviceDefault_NoActionBar_TranslucentDecor}, and
         * {@link android.R.style#Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor}.</p>
         *
         * <p>When this flag is enabled for a window, it automatically sets
         * the system UI visibility flags {@link View#SYSTEM_UI_FLAG_LAYOUT_STABLE} and
         * {@link View#SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN}.</p>
         */
        public static final int FLAG_TRANSLUCENT_STATUS = 0x04000000;

        /**
         * Window flag: request a translucent navigation bar with minimal system-provided
         * background protection.
         *
         * <p>This flag can be controlled in your theme through the
         * {@link android.R.attr#windowTranslucentNavigation} attribute; this attribute
         * is automatically set for you in the standard translucent decor themes
         * such as
         * {@link android.R.style#Theme_Holo_NoActionBar_TranslucentDecor},
         * {@link android.R.style#Theme_Holo_Light_NoActionBar_TranslucentDecor},
         * {@link android.R.style#Theme_DeviceDefault_NoActionBar_TranslucentDecor}, and
         * {@link android.R.style#Theme_DeviceDefault_Light_NoActionBar_TranslucentDecor}.</p>
         *
         * <p>When this flag is enabled for a window, it automatically sets
         * the system UI visibility flags {@link View#SYSTEM_UI_FLAG_LAYOUT_STABLE} and
         * {@link View#SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION}.</p>
         */
        public static final int FLAG_TRANSLUCENT_NAVIGATION = 0x08000000;

        /**
         * Flag for a window in local focus mode.
         * Window in local focus mode can control focus independent of window manager using
         * {@link Window#setLocalFocus(boolean, boolean)}.
         * Usually window in this mode will not get touch/key events from window manager, but will
         * get events only via local injection using {@link Window#injectInputEvent(InputEvent)}.
         */
        public static final int FLAG_LOCAL_FOCUS_MODE = 0x10000000;

        /** Window flag: Enable touches to slide out of a window into neighboring
         * windows in mid-gesture instead of being captured for the duration of
         * the gesture.
         *
         * This flag changes the behavior of touch focus for this window only.
         * Touches can slide out of the window but they cannot necessarily slide
         * back in (unless the other window with touch focus permits it).
         *
         * {@hide}
         */
        public static final int FLAG_SLIPPERY = 0x20000000;

        /**
         * Window flag: When requesting layout with an attached window, the attached window may
         * overlap with the screen decorations of the parent window such as the navigation bar. By
         * including this flag, the window manager will layout the attached window within the decor
         * frame of the parent window such that it doesn't overlap with screen decorations.
         */
        public static final int FLAG_LAYOUT_ATTACHED_IN_DECOR = 0x40000000;

        /**
         * Flag indicating that this Window is responsible for drawing the background for the
         * system bars. If set, the system bars are drawn with a transparent background and the
         * corresponding areas in this window are filled with the colors specified in
         * {@link Window#getStatusBarColor()} and {@link Window#getNavigationBarColor()}.
         */
        public static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = 0x80000000;

        /**
         * Various behavioral options/flags.  Default is none.
         *
         * @see #FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
         * @see #FLAG_DIM_BEHIND
         * @see #FLAG_NOT_FOCUSABLE
         * @see #FLAG_NOT_TOUCHABLE
         * @see #FLAG_NOT_TOUCH_MODAL
         * @see #FLAG_TOUCHABLE_WHEN_WAKING
         * @see #FLAG_KEEP_SCREEN_ON
         * @see #FLAG_LAYOUT_IN_SCREEN
         * @see #FLAG_LAYOUT_NO_LIMITS
         * @see #FLAG_FULLSCREEN
         * @see #FLAG_FORCE_NOT_FULLSCREEN
         * @see #FLAG_SECURE
         * @see #FLAG_SCALED
         * @see #FLAG_IGNORE_CHEEK_PRESSES
         * @see #FLAG_LAYOUT_INSET_DECOR
         * @see #FLAG_ALT_FOCUSABLE_IM
         * @see #FLAG_WATCH_OUTSIDE_TOUCH
         * @see #FLAG_SHOW_WHEN_LOCKED
         * @see #FLAG_SHOW_WALLPAPER
         * @see #FLAG_TURN_SCREEN_ON
         * @see #FLAG_DISMISS_KEYGUARD
         * @see #FLAG_SPLIT_TOUCH
         * @see #FLAG_HARDWARE_ACCELERATED
         * @see #FLAG_LOCAL_FOCUS_MODE
         * @see #FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
         */
        @ViewDebug.ExportedProperty(flagMapping = {
                @ViewDebug.FlagToString(mask = FLAG_ALLOW_LOCK_WHILE_SCREEN_ON, equals = FLAG_ALLOW_LOCK_WHILE_SCREEN_ON,
                        name = "FLAG_ALLOW_LOCK_WHILE_SCREEN_ON"),
                @ViewDebug.FlagToString(mask = FLAG_DIM_BEHIND, equals = FLAG_DIM_BEHIND,
                        name = "FLAG_DIM_BEHIND"),
                @ViewDebug.FlagToString(mask = FLAG_BLUR_BEHIND, equals = FLAG_BLUR_BEHIND,
                        name = "FLAG_BLUR_BEHIND"),
                @ViewDebug.FlagToString(mask = FLAG_NOT_FOCUSABLE, equals = FLAG_NOT_FOCUSABLE,
                        name = "FLAG_NOT_FOCUSABLE"),
                @ViewDebug.FlagToString(mask = FLAG_NOT_TOUCHABLE, equals = FLAG_NOT_TOUCHABLE,
                        name = "FLAG_NOT_TOUCHABLE"),
                @ViewDebug.FlagToString(mask = FLAG_NOT_TOUCH_MODAL, equals = FLAG_NOT_TOUCH_MODAL,
                        name = "FLAG_NOT_TOUCH_MODAL"),
                @ViewDebug.FlagToString(mask = FLAG_TOUCHABLE_WHEN_WAKING, equals = FLAG_TOUCHABLE_WHEN_WAKING,
                        name = "FLAG_TOUCHABLE_WHEN_WAKING"),
                @ViewDebug.FlagToString(mask = FLAG_KEEP_SCREEN_ON, equals = FLAG_KEEP_SCREEN_ON,
                        name = "FLAG_KEEP_SCREEN_ON"),
                @ViewDebug.FlagToString(mask = FLAG_LAYOUT_IN_SCREEN, equals = FLAG_LAYOUT_IN_SCREEN,
                        name = "FLAG_LAYOUT_IN_SCREEN"),
                @ViewDebug.FlagToString(mask = FLAG_LAYOUT_NO_LIMITS, equals = FLAG_LAYOUT_NO_LIMITS,
                        name = "FLAG_LAYOUT_NO_LIMITS"),
                @ViewDebug.FlagToString(mask = FLAG_FULLSCREEN, equals = FLAG_FULLSCREEN,
                        name = "FLAG_FULLSCREEN"),
                @ViewDebug.FlagToString(mask = FLAG_FORCE_NOT_FULLSCREEN, equals = FLAG_FORCE_NOT_FULLSCREEN,
                        name = "FLAG_FORCE_NOT_FULLSCREEN"),
                @ViewDebug.FlagToString(mask = FLAG_DITHER, equals = FLAG_DITHER,
                        name = "FLAG_DITHER"),
                @ViewDebug.FlagToString(mask = FLAG_SECURE, equals = FLAG_SECURE,
                        name = "FLAG_SECURE"),
                @ViewDebug.FlagToString(mask = FLAG_SCALED, equals = FLAG_SCALED,
                        name = "FLAG_SCALED"),
                @ViewDebug.FlagToString(mask = FLAG_IGNORE_CHEEK_PRESSES, equals = FLAG_IGNORE_CHEEK_PRESSES,
                        name = "FLAG_IGNORE_CHEEK_PRESSES"),
                @ViewDebug.FlagToString(mask = FLAG_LAYOUT_INSET_DECOR, equals = FLAG_LAYOUT_INSET_DECOR,
                        name = "FLAG_LAYOUT_INSET_DECOR"),
                @ViewDebug.FlagToString(mask = FLAG_ALT_FOCUSABLE_IM, equals = FLAG_ALT_FOCUSABLE_IM,
                        name = "FLAG_ALT_FOCUSABLE_IM"),
                @ViewDebug.FlagToString(mask = FLAG_WATCH_OUTSIDE_TOUCH, equals = FLAG_WATCH_OUTSIDE_TOUCH,
                        name = "FLAG_WATCH_OUTSIDE_TOUCH"),
                @ViewDebug.FlagToString(mask = FLAG_SHOW_WHEN_LOCKED, equals = FLAG_SHOW_WHEN_LOCKED,
                        name = "FLAG_SHOW_WHEN_LOCKED"),
                @ViewDebug.FlagToString(mask = FLAG_SHOW_WALLPAPER, equals = FLAG_SHOW_WALLPAPER,
                        name = "FLAG_SHOW_WALLPAPER"),
                @ViewDebug.FlagToString(mask = FLAG_TURN_SCREEN_ON, equals = FLAG_TURN_SCREEN_ON,
                        name = "FLAG_TURN_SCREEN_ON"),
                @ViewDebug.FlagToString(mask = FLAG_DISMISS_KEYGUARD, equals = FLAG_DISMISS_KEYGUARD,
                        name = "FLAG_DISMISS_KEYGUARD"),
                @ViewDebug.FlagToString(mask = FLAG_SPLIT_TOUCH, equals = FLAG_SPLIT_TOUCH,
                        name = "FLAG_SPLIT_TOUCH"),
                @ViewDebug.FlagToString(mask = FLAG_HARDWARE_ACCELERATED, equals = FLAG_HARDWARE_ACCELERATED,
                        name = "FLAG_HARDWARE_ACCELERATED"),
                @ViewDebug.FlagToString(mask = FLAG_LOCAL_FOCUS_MODE, equals = FLAG_LOCAL_FOCUS_MODE,
                        name = "FLAG_LOCAL_FOCUS_MODE"),
                @ViewDebug.FlagToString(mask = FLAG_TRANSLUCENT_STATUS, equals = FLAG_TRANSLUCENT_STATUS,
                        name = "FLAG_TRANSLUCENT_STATUS"),
                @ViewDebug.FlagToString(mask = FLAG_TRANSLUCENT_NAVIGATION, equals = FLAG_TRANSLUCENT_NAVIGATION,
                        name = "FLAG_TRANSLUCENT_NAVIGATION"),
                @ViewDebug.FlagToString(mask = FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, equals = FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                        name = "FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS")
        }, formatToHexString = true)
        public int flags;

        /**
         * If the window has requested hardware acceleration, but this is not
         * allowed in the process it is in, then still render it as if it is
         * hardware accelerated.  This is used for the starting preview windows
         * in the system process, which don't need to have the overhead of
         * hardware acceleration (they are just a static rendering), but should
         * be rendered as such to match the actual window of the app even if it
         * is hardware accelerated.
         * Even if the window isn't hardware accelerated, still do its rendering
         * as if it was.
         * Like {@link #FLAG_HARDWARE_ACCELERATED} except for trusted system windows
         * that need hardware acceleration (e.g. LockScreen), where hardware acceleration
         * is generally disabled. This flag must be specified in addition to
         * {@link #FLAG_HARDWARE_ACCELERATED} to enable hardware acceleration for system
         * windows.
         *
         * @hide
         */
        public static final int PRIVATE_FLAG_FAKE_HARDWARE_ACCELERATED = 0x00000001;

        /**
         * In the system process, we globally do not use hardware acceleration
         * because there are many threads doing UI there and they conflict.
         * If certain parts of the UI that really do want to use hardware
         * acceleration, this flag can be set to force it.  This is basically
         * for the lock screen.  Anyone else using it, you are probably wrong.
         *
         * @hide
         */
        public static final int PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED = 0x00000002;

        /**
         * By default, wallpapers are sent new offsets when the wallpaper is scrolled. Wallpapers
         * may elect to skip these notifications if they are not doing anything productive with
         * them (they do not affect the wallpaper scrolling operation) by calling
         * {@link
         * android.service.wallpaper.WallpaperService.Engine#setOffsetNotificationsEnabled(boolean)}.
         *
         * @hide
         */
        public static final int PRIVATE_FLAG_WANTS_OFFSET_NOTIFICATIONS = 0x00000004;

        /** In a multiuser system if this flag is set and the owner is a system process then this
         * window will appear on all user screens. This overrides the default behavior of window
         * types that normally only appear on the owning user's screen. Refer to each window type
         * to determine its default behavior.
         *
         * {@hide} */
        public static final int PRIVATE_FLAG_SHOW_FOR_ALL_USERS = 0x00000010;

        /**
         * Never animate position changes of the window.
         *
         * {@hide} */
        public static final int PRIVATE_FLAG_NO_MOVE_ANIMATION = 0x00000040;

        /** Window flag: special flag to limit the size of the window to be
         * original size ([320x480] x density). Used to create window for applications
         * running under compatibility mode.
         *
         * {@hide} */
        public static final int PRIVATE_FLAG_COMPATIBLE_WINDOW = 0x00000080;

        /** Window flag: a special option intended for system dialogs.  When
         * this flag is set, the window will demand focus unconditionally when
         * it is created.
         * {@hide} */
        public static final int PRIVATE_FLAG_SYSTEM_ERROR = 0x00000100;

        /** Window flag: maintain the previous translucent decor state when this window
         * becomes top-most.
         * {@hide} */
        public static final int PRIVATE_FLAG_INHERIT_TRANSLUCENT_DECOR = 0x00000200;

        /**
         * Flag whether the current window is a keyguard window, meaning that it will hide all other
         * windows behind it except for windows with flag {@link #FLAG_SHOW_WHEN_LOCKED} set.
         * Further, this can only be set by {@link LayoutParams#TYPE_STATUS_BAR}.
         * {@hide}
         */
        public static final int PRIVATE_FLAG_KEYGUARD = 0x00000400;

        /**
         * Flag that prevents the wallpaper behind the current window from receiving touch events.
         *
         * {@hide}
         */
        public static final int PRIVATE_FLAG_DISABLE_WALLPAPER_TOUCH_EVENTS = 0x00000800;

        /**
         * Flag to force the status bar window to be visible all the time. If the bar is hidden when
         * this flag is set it will be shown again and the bar will have a transparent background.
         * This can only be set by {@link LayoutParams#TYPE_STATUS_BAR}.
         *
         * {@hide}
         */
        public static final int PRIVATE_FLAG_FORCE_STATUS_BAR_VISIBLE_TRANSPARENT = 0x00001000;

        /**
         * Flag indicating that the x, y, width, and height members should be
         * ignored (and thus their previous value preserved). For example
         * because they are being managed externally through repositionChild.
         *
         * {@hide}
         */
        public static final int PRIVATE_FLAG_PRESERVE_GEOMETRY = 0x00002000;

        /**
         * Flag that will make window ignore app visibility and instead depend purely on the decor
         * view visibility for determining window visibility. This is used by recents to keep
         * drawing after it launches an app.
         * @hide
         */
        public static final int PRIVATE_FLAG_FORCE_DECOR_VIEW_VISIBILITY = 0x00004000;

        /**
         * Flag to indicate that this window is not expected to be replaced across
         * configuration change triggered activity relaunches. In general the WindowManager
         * expects Windows to be replaced after relaunch, and thus it will preserve their surfaces
         * until the replacement is ready to show in order to prevent visual glitch. However
         * some windows, such as PopupWindows expect to be cleared across configuration change,
         * and thus should hint to the WindowManager that it should not wait for a replacement.
         * @hide
         */
        public static final int PRIVATE_FLAG_WILL_NOT_REPLACE_ON_RELAUNCH = 0x00008000;

        /**
         * Flag to indicate that this child window should always be laid-out in the parent
         * frame regardless of the current windowing mode configuration.
         * @hide
         */
        public static final int PRIVATE_FLAG_LAYOUT_CHILD_WINDOW_IN_PARENT_FRAME = 0x00010000;

        /**
         * Flag to indicate that this window is always drawing the status bar background, no matter
         * what the other flags are.
         * @hide
         */
        public static final int PRIVATE_FLAG_FORCE_DRAW_STATUS_BAR_BACKGROUND = 0x00020000;

        /**
         * Flag to indicate that this window needs Sustained Performance Mode if
         * the device supports it.
         * @hide
         */
        public static final int PRIVATE_FLAG_SUSTAINED_PERFORMANCE_MODE = 0x00040000;

        /**
         * Control flags that are private to the platform.
         * @hide
         */
        public int privateFlags;

        /**
         * Value for {@link #needsMenuKey} for a window that has not explicitly specified if it
         * needs {@link #NEEDS_MENU_SET_TRUE} or doesn't need {@link #NEEDS_MENU_SET_FALSE} a menu
         * key. For this case, we should look at windows behind it to determine the appropriate
         * value.
         *
         * @hide
         */
        public static final int NEEDS_MENU_UNSET = 0;

        /**
         * Value for {@link #needsMenuKey} for a window that has explicitly specified it needs a
         * menu key.
         *
         * @hide
         */
        public static final int NEEDS_MENU_SET_TRUE = 1;

        /**
         * Value for {@link #needsMenuKey} for a window that has explicitly specified it doesn't
         * needs a menu key.
         *
         * @hide
         */
        public static final int NEEDS_MENU_SET_FALSE = 2;

        /**
         * State variable for a window belonging to an activity that responds to
         * {@link KeyEvent#KEYCODE_MENU} and therefore needs a Menu key. For devices where Menu is a
         * physical button this variable is ignored, but on devices where the Menu key is drawn in
         * software it may be hidden unless this variable is set to {@link #NEEDS_MENU_SET_TRUE}.
         *
         *  (Note that Action Bars, when available, are the preferred way to offer additional
         * functions otherwise accessed via an options menu.)
         *
         * {@hide}
         */
        public int needsMenuKey = NEEDS_MENU_UNSET;

        /**
         * Given a particular set of window manager flags, determine whether
         * such a window may be a target for an input method when it has
         * focus.  In particular, this checks the
         * {@link #FLAG_NOT_FOCUSABLE} and {@link #FLAG_ALT_FOCUSABLE_IM}
         * flags and returns true if the combination of the two corresponds
         * to a window that needs to be behind the input method so that the
         * user can type into it.
         *
         * @param flags The current window manager flags.
         *
         * @return Returns true if such a window should be behind/interact
         * with an input method, false if not.
         */
        public static boolean mayUseInputMethod(int flags) {
            switch (flags&(FLAG_NOT_FOCUSABLE|FLAG_ALT_FOCUSABLE_IM)) {
                case 0:
                case FLAG_NOT_FOCUSABLE|FLAG_ALT_FOCUSABLE_IM:
                    return true;
            }
            return false;
        }

        /**
         * Mask for {@link #softInputMode} of the bits that determine the
         * desired visibility state of the soft input area for this window.
         */
        public static final int SOFT_INPUT_MASK_STATE = 0x0f;

        /**
         * Visibility state for {@link #softInputMode}: no state has been specified.
         */
        public static final int SOFT_INPUT_STATE_UNSPECIFIED = 0;

        /**
         * Visibility state for {@link #softInputMode}: please don't change the state of
         * the soft input area.
         */
        public static final int SOFT_INPUT_STATE_UNCHANGED = 1;

        /**
         * Visibility state for {@link #softInputMode}: please hide any soft input
         * area when normally appropriate (when the user is navigating
         * forward to your window).
         */
        public static final int SOFT_INPUT_STATE_HIDDEN = 2;

        /**
         * Visibility state for {@link #softInputMode}: please always hide any
         * soft input area when this window receives focus.
         */
        public static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3;

        /**
         * Visibility state for {@link #softInputMode}: please show the soft
         * input area when normally appropriate (when the user is navigating
         * forward to your window).
         */
        public static final int SOFT_INPUT_STATE_VISIBLE = 4;

        /**
         * Visibility state for {@link #softInputMode}: please always make the
         * soft input area visible when this window receives input focus.
         */
        public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;

        /**
         * Mask for {@link #softInputMode} of the bits that determine the
         * way that the window should be adjusted to accommodate the soft
         * input window.
         */
        public static final int SOFT_INPUT_MASK_ADJUST = 0xf0;

        /** Adjustment option for {@link #softInputMode}: nothing specified.
         * The system will try to pick one or
         * the other depending on the contents of the window.
         */
        public static final int SOFT_INPUT_ADJUST_UNSPECIFIED = 0x00;

        /** Adjustment option for {@link #softInputMode}: set to allow the
         * window to be resized when an input
         * method is shown, so that its contents are not covered by the input
         * method.  This can <em>not</em> be combined with
         * {@link #SOFT_INPUT_ADJUST_PAN}; if
         * neither of these are set, then the system will try to pick one or
         * the other depending on the contents of the window. If the window's
         * layout parameter flags include {@link #FLAG_FULLSCREEN}, this
         * value for {@link #softInputMode} will be ignored; the window will
         * not resize, but will stay fullscreen.
         */
        public static final int SOFT_INPUT_ADJUST_RESIZE = 0x10;

        /** Adjustment option for {@link #softInputMode}: set to have a window
         * pan when an input method is
         * shown, so it doesn't need to deal with resizing but just panned
         * by the framework to ensure the current input focus is visible.  This
         * can <em>not</em> be combined with {@link #SOFT_INPUT_ADJUST_RESIZE}; if
         * neither of these are set, then the system will try to pick one or
         * the other depending on the contents of the window.
         */
        public static final int SOFT_INPUT_ADJUST_PAN = 0x20;

        /** Adjustment option for {@link #softInputMode}: set to have a window
         * not adjust for a shown input method.  The window will not be resized,
         * and it will not be panned to make its focus visible.
         */
        public static final int SOFT_INPUT_ADJUST_NOTHING = 0x30;

        /**
         * Bit for {@link #softInputMode}: set when the user has navigated
         * forward to the window.  This is normally set automatically for
         * you by the system, though you may want to set it in certain cases
         * when you are displaying a window yourself.  This flag will always
         * be cleared automatically after the window is displayed.
         */
        public static final int SOFT_INPUT_IS_FORWARD_NAVIGATION = 0x100;

        /**
         * Desired operating mode for any soft input area.  May be any combination
         * of:
         *
         * <ul>
         * <li> One of the visibility states
         * {@link #SOFT_INPUT_STATE_UNSPECIFIED}, {@link #SOFT_INPUT_STATE_UNCHANGED},
         * {@link #SOFT_INPUT_STATE_HIDDEN}, {@link #SOFT_INPUT_STATE_ALWAYS_VISIBLE}, or
         * {@link #SOFT_INPUT_STATE_VISIBLE}.
         * <li> One of the adjustment options
         * {@link #SOFT_INPUT_ADJUST_UNSPECIFIED},
         * {@link #SOFT_INPUT_ADJUST_RESIZE}, or
         * {@link #SOFT_INPUT_ADJUST_PAN}.
         * </ul>
         *
         *
         * <p>This flag can be controlled in your theme through the
         * {@link android.R.attr#windowSoftInputMode} attribute.</p>
         */
        public int softInputMode;

        /**
         * Placement of window within the screen as per {@link Gravity}.  Both
         * {@link Gravity#apply(int, int, int, android.graphics.Rect, int, int,
         * android.graphics.Rect) Gravity.apply} and
         * {@link Gravity#applyDisplay(int, android.graphics.Rect, android.graphics.Rect)
         * Gravity.applyDisplay} are used during window layout, with this value
         * given as the desired gravity.  For example you can specify
         * {@link Gravity#DISPLAY_CLIP_HORIZONTAL Gravity.DISPLAY_CLIP_HORIZONTAL} and
         * {@link Gravity#DISPLAY_CLIP_VERTICAL Gravity.DISPLAY_CLIP_VERTICAL} here
         * to control the behavior of
         * {@link Gravity#applyDisplay(int, android.graphics.Rect, android.graphics.Rect)
         * Gravity.applyDisplay}.
         *
         * @see Gravity
         */
        public int gravity;

        /**
         * The horizontal margin, as a percentage of the container's width,
         * between the container and the widget.  See
         * {@link Gravity#apply(int, int, int, android.graphics.Rect, int, int,
         * android.graphics.Rect) Gravity.apply} for how this is used.  This
         * field is added with {@link #x} to supply the <var>xAdj</var> parameter.
         */
        public float horizontalMargin;

        /**
         * The vertical margin, as a percentage of the container's height,
         * between the container and the widget.  See
         * {@link Gravity#apply(int, int, int, android.graphics.Rect, int, int,
         * android.graphics.Rect) Gravity.apply} for how this is used.  This
         * field is added with {@link #y} to supply the <var>yAdj</var> parameter.
         */
        public float verticalMargin;

        /**
         * Positive insets between the drawing surface and window content.
         *
         * @hide
         */
        public final Rect surfaceInsets = new Rect();

        /**
         * Whether the surface insets have been manually set. When set to
         * {@code false}, the view root will automatically determine the
         * appropriate surface insets.
         *
         * @see #surfaceInsets
         * @hide
         */
        public boolean hasManualSurfaceInsets;

        /**
         * Whether the previous surface insets should be used vs. what is currently set. When set
         * to {@code true}, the view root will ignore surfaces insets in this object and use what
         * it currently has.
         *
         * @see #surfaceInsets
         * @hide
         */
        public boolean preservePreviousSurfaceInsets = true;

        /**
         * The desired bitmap format.  May be one of the constants in
         * {@link android.graphics.PixelFormat}.  Default is OPAQUE.
         */
        public int format;

        /**
         * A style resource defining the animations to use for this window.
         * This must be a system resource; it can not be an application resource
         * because the window manager does not have access to applications.
         */
        public int windowAnimations;

        /**
         * An alpha value to apply to this entire window.
         * An alpha of 1.0 means fully opaque and 0.0 means fully transparent
         */
        public float alpha = 1.0f;

        /**
         * When {@link #FLAG_DIM_BEHIND} is set, this is the amount of dimming
         * to apply.  Range is from 1.0 for completely opaque to 0.0 for no
         * dim.
         */
        public float dimAmount = 1.0f;

        /**
         * Default value for {@link #screenBrightness} and {@link #buttonBrightness}
         * indicating that the brightness value is not overridden for this window
         * and normal brightness policy should be used.
         */
        public static final float BRIGHTNESS_OVERRIDE_NONE = -1.0f;

        /**
         * Value for {@link #screenBrightness} and {@link #buttonBrightness}
         * indicating that the screen or button backlight brightness should be set
         * to the lowest value when this window is in front.
         */
        public static final float BRIGHTNESS_OVERRIDE_OFF = 0.0f;

        /**
         * Value for {@link #screenBrightness} and {@link #buttonBrightness}
         * indicating that the screen or button backlight brightness should be set
         * to the hightest value when this window is in front.
         */
        public static final float BRIGHTNESS_OVERRIDE_FULL = 1.0f;

        /**
         * This can be used to override the user's preferred brightness of
         * the screen.  A value of less than 0, the default, means to use the
         * preferred screen brightness.  0 to 1 adjusts the brightness from
         * dark to full bright.
         */
        public float screenBrightness = BRIGHTNESS_OVERRIDE_NONE;

        /**
         * This can be used to override the standard behavior of the button and
         * keyboard backlights.  A value of less than 0, the default, means to
         * use the standard backlight behavior.  0 to 1 adjusts the brightness
         * from dark to full bright.
         */
        public float buttonBrightness = BRIGHTNESS_OVERRIDE_NONE;

        /**
         * Value for {@link #rotationAnimation} to define the animation used to
         * specify that this window will rotate in or out following a rotation.
         */
        public static final int ROTATION_ANIMATION_ROTATE = 0;

        /**
         * Value for {@link #rotationAnimation} to define the animation used to
         * specify that this window will fade in or out following a rotation.
         */
        public static final int ROTATION_ANIMATION_CROSSFADE = 1;

        /**
         * Value for {@link #rotationAnimation} to define the animation used to
         * specify that this window will immediately disappear or appear following
         * a rotation.
         */
        public static final int ROTATION_ANIMATION_JUMPCUT = 2;

        /**
         * Value for {@link #rotationAnimation} to specify seamless rotation mode.
         * This works like JUMPCUT but will fall back to CROSSFADE if rotation
         * can't be applied without pausing the screen.
         *
         * @hide
         */
        public static final int ROTATION_ANIMATION_SEAMLESS = 3;

        /**
         * Define the exit and entry animations used on this window when the device is rotated.
         * This only has an affect if the incoming and outgoing topmost
         * opaque windows have the #FLAG_FULLSCREEN bit set and are not covered
         * by other windows. All other situations default to the
         * {@link #ROTATION_ANIMATION_ROTATE} behavior.
         *
         * @see #ROTATION_ANIMATION_ROTATE
         * @see #ROTATION_ANIMATION_CROSSFADE
         * @see #ROTATION_ANIMATION_JUMPCUT
         */
        public int rotationAnimation = ROTATION_ANIMATION_ROTATE;

        /**
         * Identifier for this window.  This will usually be filled in for
         * you.
         */
        public IBinder token = null;

        /**
         * Name of the package owning this window.
         */
        public String packageName = null;

        /**
         * Specific orientation value for a window.
         * May be any of the same values allowed
         * for {@link android.content.pm.ActivityInfo#screenOrientation}.
         * If not set, a default value of
         * {@link android.content.pm.ActivityInfo#SCREEN_ORIENTATION_UNSPECIFIED}
         * will be used.
         */
        public int screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        /**
         * The preferred refresh rate for the window.
         *
         * This must be one of the supported refresh rates obtained for the display(s) the window
         * is on. The selected refresh rate will be applied to the display's default mode.
         *
         * This value is ignored if {@link #preferredDisplayModeId} is set.
         *
         * @see Display#getSupportedRefreshRates()
         * @deprecated use {@link #preferredDisplayModeId} instead
         */
        @Deprecated
        public float preferredRefreshRate;

        /**
         * Id of the preferred display mode for the window.
         * <p>
         * This must be one of the supported modes obtained for the display(s) the window is on.
         * A value of {@code 0} means no preference.
         *
         * @see Display#getSupportedModes()
         * @see Display.Mode#getModeId()
         */
        public int preferredDisplayModeId;

        /**
         * Control the visibility of the status bar.
         *
         * @see View#STATUS_BAR_VISIBLE
         * @see View#STATUS_BAR_HIDDEN
         */
        public int systemUiVisibility;

        /**
         * @hide
         * The ui visibility as requested by the views in this hierarchy.
         * the combined value should be systemUiVisibility | subtreeSystemUiVisibility.
         */
        public int subtreeSystemUiVisibility;

        /**
         * Get callbacks about the system ui visibility changing.
         *
         * TODO: Maybe there should be a bitfield of optional callbacks that we need.
         *
         * @hide
         */
        public boolean hasSystemUiListeners;

        /**
         * When this window has focus, disable touch pad pointer gesture processing.
         * The window will receive raw position updates from the touch pad instead
         * of pointer movements and synthetic touch events.
         *
         * @hide
         */
        public static final int INPUT_FEATURE_DISABLE_POINTER_GESTURES = 0x00000001;

        /**
         * Does not construct an input channel for this window.  The channel will therefore
         * be incapable of receiving input.
         *
         * @hide
         */
        public static final int INPUT_FEATURE_NO_INPUT_CHANNEL = 0x00000002;

        /**
         * When this window has focus, does not call user activity for all input events so
         * the application will have to do it itself.  Should only be used by
         * the keyguard and phone app.
         * <p>
         * Should only be used by the keyguard and phone app.
         * </p>
         *
         * @hide
         */
        public static final int INPUT_FEATURE_DISABLE_USER_ACTIVITY = 0x00000004;

        /**
         * Control special features of the input subsystem.
         *
         * @see #INPUT_FEATURE_DISABLE_POINTER_GESTURES
         * @see #INPUT_FEATURE_NO_INPUT_CHANNEL
         * @see #INPUT_FEATURE_DISABLE_USER_ACTIVITY
         * @hide
         */
        public int inputFeatures;

        /**
         * Sets the number of milliseconds before the user activity timeout occurs
         * when this window has focus.  A value of -1 uses the standard timeout.
         * A value of 0 uses the minimum support display timeout.
         * <p>
         * This property can only be used to reduce the user specified display timeout;
         * it can never make the timeout longer than it normally would be.
         * </p><p>
         * Should only be used by the keyguard and phone app.
         * </p>
         *
         * @hide
         */
        public long userActivityTimeout = -1;

        /**
         * For windows with an anchor (e.g. PopupWindow), keeps track of the View that anchors the
         * window.
         *
         * @hide
         */
        public int accessibilityIdOfAnchor = -1;

        /**
         * The window title isn't kept in sync with what is displayed in the title bar, so we
         * separately track the currently shown title to provide to accessibility.
         *
         * @hide
         */
        public CharSequence accessibilityTitle;

        /**
         * Sets a timeout in milliseconds before which the window will be hidden
         * by the window manager. Useful for transient notifications like toasts
         * so we don't have to rely on client cooperation to ensure the window
         * is hidden. Must be specified at window creation time. Note that apps
         * are not prepared to handle their windows being removed without their
         * explicit request and may try to interact with the removed window
         * resulting in undefined behavior and crashes. Therefore, we do hide
         * such windows to prevent them from overlaying other apps.
         *
         * @hide
         */
        public long hideTimeoutMilliseconds = -1;

        public LayoutParams() {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            type = TYPE_APPLICATION;
            format = PixelFormat.OPAQUE;
        }

        public LayoutParams(int _type) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            type = _type;
            format = PixelFormat.OPAQUE;
        }

        public LayoutParams(int _type, int _flags) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            type = _type;
            flags = _flags;
            format = PixelFormat.OPAQUE;
        }

        public LayoutParams(int _type, int _flags, int _format) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            type = _type;
            flags = _flags;
            format = _format;
        }

        public LayoutParams(int w, int h, int _type, int _flags, int _format) {
            super(w, h);
            type = _type;
            flags = _flags;
            format = _format;
        }

        public LayoutParams(int w, int h, int xpos, int ypos, int _type,
                            int _flags, int _format) {
            super(w, h);
            x = xpos;
            y = ypos;
            type = _type;
            flags = _flags;
            format = _format;
        }

        public final void setTitle(CharSequence title) {
            if (null == title)
                title = "";

            mTitle = TextUtils.stringOrSpannedString(title);
        }

        public final CharSequence getTitle() {
            return mTitle != null ? mTitle : "";
        }

        /**
         * Sets the surface insets based on the elevation (visual z position) of the input view.
         * @hide
         */
        public final void setSurfaceInsets(View view, boolean manual, boolean preservePrevious) {
            final int surfaceInset = (int) Math.ceil(view.getZ() * 2);
            // Partial workaround for b/28318973. Every inset change causes a freeform window
            // to jump a little for a few frames. If we never allow surface insets to decrease,
            // they will stabilize quickly (often from the very beginning, as most windows start
            // as focused).
            // TODO(b/22668382) to fix this properly.
            if (surfaceInset == 0) {
                // OK to have 0 (this is the case for non-freeform windows).
                surfaceInsets.set(0, 0, 0, 0);
            } else {
                surfaceInsets.set(
                        Math.max(surfaceInset, surfaceInsets.left),
                        Math.max(surfaceInset, surfaceInsets.top),
                        Math.max(surfaceInset, surfaceInsets.right),
                        Math.max(surfaceInset, surfaceInsets.bottom));
            }
            hasManualSurfaceInsets = manual;
            preservePreviousSurfaceInsets = preservePrevious;
        }

        /** @hide */
        @SystemApi
        public final void setUserActivityTimeout(long timeout) {
            userActivityTimeout = timeout;
        }

        /** @hide */
        @SystemApi
        public final long getUserActivityTimeout() {
            return userActivityTimeout;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int parcelableFlags) {
            out.writeInt(width);
            out.writeInt(height);
            out.writeInt(x);
            out.writeInt(y);
            out.writeInt(type);
            out.writeInt(flags);
            out.writeInt(privateFlags);
            out.writeInt(softInputMode);
            out.writeInt(gravity);
            out.writeFloat(horizontalMargin);
            out.writeFloat(verticalMargin);
            out.writeInt(format);
            out.writeInt(windowAnimations);
            out.writeFloat(alpha);
            out.writeFloat(dimAmount);
            out.writeFloat(screenBrightness);
            out.writeFloat(buttonBrightness);
            out.writeInt(rotationAnimation);
            out.writeStrongBinder(token);
            out.writeString(packageName);
            TextUtils.writeToParcel(mTitle, out, parcelableFlags);
            out.writeInt(screenOrientation);
            out.writeFloat(preferredRefreshRate);
            out.writeInt(preferredDisplayModeId);
            out.writeInt(systemUiVisibility);
            out.writeInt(subtreeSystemUiVisibility);
            out.writeInt(hasSystemUiListeners ? 1 : 0);
            out.writeInt(inputFeatures);
            out.writeLong(userActivityTimeout);
            out.writeInt(surfaceInsets.left);
            out.writeInt(surfaceInsets.top);
            out.writeInt(surfaceInsets.right);
            out.writeInt(surfaceInsets.bottom);
            out.writeInt(hasManualSurfaceInsets ? 1 : 0);
            out.writeInt(preservePreviousSurfaceInsets ? 1 : 0);
            out.writeInt(needsMenuKey);
            out.writeInt(accessibilityIdOfAnchor);
            TextUtils.writeToParcel(accessibilityTitle, out, parcelableFlags);
            out.writeLong(hideTimeoutMilliseconds);
        }

        public static final Parcelable.Creator<LayoutParams> CREATOR
                = new Parcelable.Creator<LayoutParams>() {
            public LayoutParams createFromParcel(Parcel in) {
                return new LayoutParams(in);
            }

            public LayoutParams[] newArray(int size) {
                return new LayoutParams[size];
            }
        };


        public LayoutParams(Parcel in) {
            width = in.readInt();
            height = in.readInt();
            x = in.readInt();
            y = in.readInt();
            type = in.readInt();
            flags = in.readInt();
            privateFlags = in.readInt();
            softInputMode = in.readInt();
            gravity = in.readInt();
            horizontalMargin = in.readFloat();
            verticalMargin = in.readFloat();
            format = in.readInt();
            windowAnimations = in.readInt();
            alpha = in.readFloat();
            dimAmount = in.readFloat();
            screenBrightness = in.readFloat();
            buttonBrightness = in.readFloat();
            rotationAnimation = in.readInt();
            token = in.readStrongBinder();
            packageName = in.readString();
            mTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            screenOrientation = in.readInt();
            preferredRefreshRate = in.readFloat();
            preferredDisplayModeId = in.readInt();
            systemUiVisibility = in.readInt();
            subtreeSystemUiVisibility = in.readInt();
            hasSystemUiListeners = in.readInt() != 0;
            inputFeatures = in.readInt();
            userActivityTimeout = in.readLong();
            surfaceInsets.left = in.readInt();
            surfaceInsets.top = in.readInt();
            surfaceInsets.right = in.readInt();
            surfaceInsets.bottom = in.readInt();
            hasManualSurfaceInsets = in.readInt() != 0;
            preservePreviousSurfaceInsets = in.readInt() != 0;
            needsMenuKey = in.readInt();
            accessibilityIdOfAnchor = in.readInt();
            accessibilityTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            hideTimeoutMilliseconds = in.readLong();
        }

        @SuppressWarnings({"PointlessBitwiseExpression"})
        public static final int LAYOUT_CHANGED = 1<<0;
        public static final int TYPE_CHANGED = 1<<1;
        public static final int FLAGS_CHANGED = 1<<2;
        public static final int FORMAT_CHANGED = 1<<3;
        public static final int ANIMATION_CHANGED = 1<<4;
        public static final int DIM_AMOUNT_CHANGED = 1<<5;
        public static final int TITLE_CHANGED = 1<<6;
        public static final int ALPHA_CHANGED = 1<<7;
        public static final int MEMORY_TYPE_CHANGED = 1<<8;
        public static final int SOFT_INPUT_MODE_CHANGED = 1<<9;
        public static final int SCREEN_ORIENTATION_CHANGED = 1<<10;
        public static final int SCREEN_BRIGHTNESS_CHANGED = 1<<11;
        public static final int ROTATION_ANIMATION_CHANGED = 1<<12;
        /** {@hide} */
        public static final int BUTTON_BRIGHTNESS_CHANGED = 1<<13;
        /** {@hide} */
        public static final int SYSTEM_UI_VISIBILITY_CHANGED = 1<<14;
        /** {@hide} */
        public static final int SYSTEM_UI_LISTENER_CHANGED = 1<<15;
        /** {@hide} */
        public static final int INPUT_FEATURES_CHANGED = 1<<16;
        /** {@hide} */
        public static final int PRIVATE_FLAGS_CHANGED = 1<<17;
        /** {@hide} */
        public static final int USER_ACTIVITY_TIMEOUT_CHANGED = 1<<18;
        /** {@hide} */
        public static final int TRANSLUCENT_FLAGS_CHANGED = 1<<19;
        /** {@hide} */
        public static final int SURFACE_INSETS_CHANGED = 1<<20;
        /** {@hide} */
        public static final int PREFERRED_REFRESH_RATE_CHANGED = 1 << 21;
        /** {@hide} */
        public static final int NEEDS_MENU_KEY_CHANGED = 1 << 22;
        /** {@hide} */
        public static final int PREFERRED_DISPLAY_MODE_ID = 1 << 23;
        /** {@hide} */
        public static final int ACCESSIBILITY_ANCHOR_CHANGED = 1 << 24;
        /** {@hide} */
        public static final int ACCESSIBILITY_TITLE_CHANGED = 1 << 25;
        /** {@hide} */
        public static final int EVERYTHING_CHANGED = 0xffffffff;

        // internal buffer to backup/restore parameters under compatibility mode.
        private int[] mCompatibilityParamsBackup = null;

        public final int copyFrom(LayoutParams o) {
            int changes = 0;

            if (width != o.width) {
                width = o.width;
                changes |= LAYOUT_CHANGED;
            }
            if (height != o.height) {
                height = o.height;
                changes |= LAYOUT_CHANGED;
            }
            if (x != o.x) {
                x = o.x;
                changes |= LAYOUT_CHANGED;
            }
            if (y != o.y) {
                y = o.y;
                changes |= LAYOUT_CHANGED;
            }
            if (horizontalWeight != o.horizontalWeight) {
                horizontalWeight = o.horizontalWeight;
                changes |= LAYOUT_CHANGED;
            }
            if (verticalWeight != o.verticalWeight) {
                verticalWeight = o.verticalWeight;
                changes |= LAYOUT_CHANGED;
            }
            if (horizontalMargin != o.horizontalMargin) {
                horizontalMargin = o.horizontalMargin;
                changes |= LAYOUT_CHANGED;
            }
            if (verticalMargin != o.verticalMargin) {
                verticalMargin = o.verticalMargin;
                changes |= LAYOUT_CHANGED;
            }
            if (type != o.type) {
                type = o.type;
                changes |= TYPE_CHANGED;
            }
            if (flags != o.flags) {
                final int diff = flags ^ o.flags;
                if ((diff & (FLAG_TRANSLUCENT_STATUS | FLAG_TRANSLUCENT_NAVIGATION)) != 0) {
                    changes |= TRANSLUCENT_FLAGS_CHANGED;
                }
                flags = o.flags;
                changes |= FLAGS_CHANGED;
            }
            if (privateFlags != o.privateFlags) {
                privateFlags = o.privateFlags;
                changes |= PRIVATE_FLAGS_CHANGED;
            }
            if (softInputMode != o.softInputMode) {
                softInputMode = o.softInputMode;
                changes |= SOFT_INPUT_MODE_CHANGED;
            }
            if (gravity != o.gravity) {
                gravity = o.gravity;
                changes |= LAYOUT_CHANGED;
            }
            if (format != o.format) {
                format = o.format;
                changes |= FORMAT_CHANGED;
            }
            if (windowAnimations != o.windowAnimations) {
                windowAnimations = o.windowAnimations;
                changes |= ANIMATION_CHANGED;
            }
            if (token == null) {
                // NOTE: token only copied if the recipient doesn't
                // already have one.
                token = o.token;
            }
            if (packageName == null) {
                // NOTE: packageName only copied if the recipient doesn't
                // already have one.
                packageName = o.packageName;
            }
            if (!Objects.equals(mTitle, o.mTitle) && o.mTitle != null) {
                // NOTE: mTitle only copied if the originator set one.
                mTitle = o.mTitle;
                changes |= TITLE_CHANGED;
            }
            if (alpha != o.alpha) {
                alpha = o.alpha;
                changes |= ALPHA_CHANGED;
            }
            if (dimAmount != o.dimAmount) {
                dimAmount = o.dimAmount;
                changes |= DIM_AMOUNT_CHANGED;
            }
            if (screenBrightness != o.screenBrightness) {
                screenBrightness = o.screenBrightness;
                changes |= SCREEN_BRIGHTNESS_CHANGED;
            }
            if (buttonBrightness != o.buttonBrightness) {
                buttonBrightness = o.buttonBrightness;
                changes |= BUTTON_BRIGHTNESS_CHANGED;
            }
            if (rotationAnimation != o.rotationAnimation) {
                rotationAnimation = o.rotationAnimation;
                changes |= ROTATION_ANIMATION_CHANGED;
            }

            if (screenOrientation != o.screenOrientation) {
                screenOrientation = o.screenOrientation;
                changes |= SCREEN_ORIENTATION_CHANGED;
            }

            if (preferredRefreshRate != o.preferredRefreshRate) {
                preferredRefreshRate = o.preferredRefreshRate;
                changes |= PREFERRED_REFRESH_RATE_CHANGED;
            }

            if (preferredDisplayModeId != o.preferredDisplayModeId) {
                preferredDisplayModeId = o.preferredDisplayModeId;
                changes |= PREFERRED_DISPLAY_MODE_ID;
            }

            if (systemUiVisibility != o.systemUiVisibility
                    || subtreeSystemUiVisibility != o.subtreeSystemUiVisibility) {
                systemUiVisibility = o.systemUiVisibility;
                subtreeSystemUiVisibility = o.subtreeSystemUiVisibility;
                changes |= SYSTEM_UI_VISIBILITY_CHANGED;
            }

            if (hasSystemUiListeners != o.hasSystemUiListeners) {
                hasSystemUiListeners = o.hasSystemUiListeners;
                changes |= SYSTEM_UI_LISTENER_CHANGED;
            }

            if (inputFeatures != o.inputFeatures) {
                inputFeatures = o.inputFeatures;
                changes |= INPUT_FEATURES_CHANGED;
            }

            if (userActivityTimeout != o.userActivityTimeout) {
                userActivityTimeout = o.userActivityTimeout;
                changes |= USER_ACTIVITY_TIMEOUT_CHANGED;
            }

            if (!surfaceInsets.equals(o.surfaceInsets)) {
                surfaceInsets.set(o.surfaceInsets);
                changes |= SURFACE_INSETS_CHANGED;
            }

            if (hasManualSurfaceInsets != o.hasManualSurfaceInsets) {
                hasManualSurfaceInsets = o.hasManualSurfaceInsets;
                changes |= SURFACE_INSETS_CHANGED;
            }

            if (preservePreviousSurfaceInsets != o.preservePreviousSurfaceInsets) {
                preservePreviousSurfaceInsets = o.preservePreviousSurfaceInsets;
                changes |= SURFACE_INSETS_CHANGED;
            }

            if (needsMenuKey != o.needsMenuKey) {
                needsMenuKey = o.needsMenuKey;
                changes |= NEEDS_MENU_KEY_CHANGED;
            }

            if (accessibilityIdOfAnchor != o.accessibilityIdOfAnchor) {
                accessibilityIdOfAnchor = o.accessibilityIdOfAnchor;
                changes |= ACCESSIBILITY_ANCHOR_CHANGED;
            }

            if (!Objects.equals(accessibilityTitle, o.accessibilityTitle)
                    && o.accessibilityTitle != null) {
                // NOTE: accessibilityTitle only copied if the originator set one.
                accessibilityTitle = o.accessibilityTitle;
                changes |= ACCESSIBILITY_TITLE_CHANGED;
            }

            // This can't change, it's only set at window creation time.
            hideTimeoutMilliseconds = o.hideTimeoutMilliseconds;

            return changes;
        }

        @Override
        public String debug(String output) {
            output += "Contents of " + this + ":";
            Log.d("Debug", output);
            output = super.debug("");
            Log.d("Debug", output);
            Log.d("Debug", "");
            Log.d("Debug", "WindowManager.LayoutParams={title=" + mTitle + "}");
            return "";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(256);
            sb.append("WM.LayoutParams{");
            sb.append("(");
            sb.append(x);
            sb.append(',');
            sb.append(y);
            sb.append(")(");
            sb.append((width== MATCH_PARENT ?"fill":(width==WRAP_CONTENT?"wrap":width)));
            sb.append('x');
            sb.append((height== MATCH_PARENT ?"fill":(height==WRAP_CONTENT?"wrap":height)));
            sb.append(")");
            if (horizontalMargin != 0) {
                sb.append(" hm=");
                sb.append(horizontalMargin);
            }
            if (verticalMargin != 0) {
                sb.append(" vm=");
                sb.append(verticalMargin);
            }
            if (gravity != 0) {
                sb.append(" gr=#");
                sb.append(Integer.toHexString(gravity));
            }
            if (softInputMode != 0) {
                sb.append(" sim=#");
                sb.append(Integer.toHexString(softInputMode));
            }
            sb.append(" ty=");
            sb.append(type);
            sb.append(" fl=#");
            sb.append(Integer.toHexString(flags));
            if (privateFlags != 0) {
                if ((privateFlags & PRIVATE_FLAG_COMPATIBLE_WINDOW) != 0) {
                    sb.append(" compatible=true");
                }
                sb.append(" pfl=0x").append(Integer.toHexString(privateFlags));
            }
            if (format != PixelFormat.OPAQUE) {
                sb.append(" fmt=");
                sb.append(format);
            }
            if (windowAnimations != 0) {
                sb.append(" wanim=0x");
                sb.append(Integer.toHexString(windowAnimations));
            }
            if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                sb.append(" or=");
                sb.append(screenOrientation);
            }
            if (alpha != 1.0f) {
                sb.append(" alpha=");
                sb.append(alpha);
            }
            if (screenBrightness != BRIGHTNESS_OVERRIDE_NONE) {
                sb.append(" sbrt=");
                sb.append(screenBrightness);
            }
            if (buttonBrightness != BRIGHTNESS_OVERRIDE_NONE) {
                sb.append(" bbrt=");
                sb.append(buttonBrightness);
            }
            if (rotationAnimation != ROTATION_ANIMATION_ROTATE) {
                sb.append(" rotAnim=");
                sb.append(rotationAnimation);
            }
            if (preferredRefreshRate != 0) {
                sb.append(" preferredRefreshRate=");
                sb.append(preferredRefreshRate);
            }
            if (preferredDisplayModeId != 0) {
                sb.append(" preferredDisplayMode=");
                sb.append(preferredDisplayModeId);
            }
            if (systemUiVisibility != 0) {
                sb.append(" sysui=0x");
                sb.append(Integer.toHexString(systemUiVisibility));
            }
            if (subtreeSystemUiVisibility != 0) {
                sb.append(" vsysui=0x");
                sb.append(Integer.toHexString(subtreeSystemUiVisibility));
            }
            if (hasSystemUiListeners) {
                sb.append(" sysuil=");
                sb.append(hasSystemUiListeners);
            }
            if (inputFeatures != 0) {
                sb.append(" if=0x").append(Integer.toHexString(inputFeatures));
            }
            if (userActivityTimeout >= 0) {
                sb.append(" userActivityTimeout=").append(userActivityTimeout);
            }
            if (surfaceInsets.left != 0 || surfaceInsets.top != 0 || surfaceInsets.right != 0 ||
                    surfaceInsets.bottom != 0 || hasManualSurfaceInsets
                    || !preservePreviousSurfaceInsets) {
                sb.append(" surfaceInsets=").append(surfaceInsets);
                if (hasManualSurfaceInsets) {
                    sb.append(" (manual)");
                }
                if (!preservePreviousSurfaceInsets) {
                    sb.append(" (!preservePreviousSurfaceInsets)");
                }
            }
            if (needsMenuKey != NEEDS_MENU_UNSET) {
                sb.append(" needsMenuKey=");
                sb.append(needsMenuKey);
            }
            sb.append('}');
            return sb.toString();
        }

        /**
         * Scale the layout params' coordinates and size.
         * @hide
         */
        public void scale(float scale) {
            x = (int) (x * scale + 0.5f);
            y = (int) (y * scale + 0.5f);
            if (width > 0) {
                width = (int) (width * scale + 0.5f);
            }
            if (height > 0) {
                height = (int) (height * scale + 0.5f);
            }
        }

        /**
         * Backup the layout parameters used in compatibility mode.
         * @see LayoutParams#restore()
         */
        void backup() {
            int[] backup = mCompatibilityParamsBackup;
            if (backup == null) {
                // we backup 4 elements, x, y, width, height
                backup = mCompatibilityParamsBackup = new int[4];
            }
            backup[0] = x;
            backup[1] = y;
            backup[2] = width;
            backup[3] = height;
        }

        /**
         * Restore the layout params' coordinates, size and gravity
         * @see LayoutParams#backup()
         */
        void restore() {
            int[] backup = mCompatibilityParamsBackup;
            if (backup != null) {
                x = backup[0];
                y = backup[1];
                width = backup[2];
                height = backup[3];
            }
        }

        private CharSequence mTitle = null;

        /** @hide */
        @Override
        protected void encodeProperties(@NonNull ViewHierarchyEncoder encoder) {
            super.encodeProperties(encoder);

            encoder.addProperty("x", x);
            encoder.addProperty("y", y);
            encoder.addProperty("horizontalWeight", horizontalWeight);
            encoder.addProperty("verticalWeight", verticalWeight);
            encoder.addProperty("type", type);
            encoder.addProperty("flags", flags);
        }
    }
}

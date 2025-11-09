import SwiftUI
import UserNotifications
import shared

@main
struct iOSApp: App(
    onRefreshFabClick: {
        notifVM.askNotificationPermission(activity: nil) // iOS braucht keine Activity
        notifVM.showNotification()
    },
    onAddFabClick: { /* optional */ }
)
 {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        UNUserNotificationCenter.current().delegate = delegate
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge])
    }
}


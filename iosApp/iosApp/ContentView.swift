import UIKit
import SwiftUI
import ComposeApp
import shared

let notifService = NotificationService()
let notifVM = AppViewModel(notificationService: notifService)

Button("Enable & Show") {
    vm.askNotificationPermission(activity: nil)
    vm.showNotification()
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}

App(
    onRefreshFabClick: {
        notifVM.askNotificationPermission(activity: nil)
        vm.sendWithPermission(
            activity: nil,
            title: "Test",
            message: "Test Test Test"
        )
    },
    onAddFabClick: { /* unverändert */ }
)




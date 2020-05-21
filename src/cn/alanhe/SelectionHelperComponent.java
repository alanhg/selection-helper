package cn.alanhe;

import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.ProjectViewPane;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.impl.status.EditorBasedWidget;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseEvent;

public class SelectionHelperComponent implements ProjectComponent {
    private final Project project;

    public SelectionHelperComponent(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened() {
        WindowManager windowManager = WindowManager.getInstance();
        StatusBar statusBar = windowManager.getStatusBar(project);
        if (statusBar == null) {
            return;
        }
        statusBar.addWidget(new MyEditorBasedWidget(project));
    }

    private static class MyEditorBasedWidget extends EditorBasedWidget
            implements StatusBarWidget.Multiframe, StatusBarWidget.TextPresentation {

        protected MyEditorBasedWidget(@NotNull Project project) {
            super(project);
        }

        @NotNull
        @Override
        public String ID() {
            return "selection helper";
        }

        @Nullable
        @Override
        public WidgetPresentation getPresentation(@NotNull PlatformType type) {
            return this;
        }

        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event) {
            update(this.getEditor());
        }

        @Override
        public void install(@NotNull StatusBar bar) {
            super.install(bar);
            ProjectView.getInstance(this.myProject)
                    .getProjectViewPaneById(ProjectViewPane.ID)
                    .getTree()
                    .addTreeSelectionListener(new MyTreeSelectionListener());
        }

        private String statusBarText = "";

        @NotNull
        @Override
        public String getText() {
            return statusBarText;
        }

        @NotNull
        @Override
        public String getMaxPossibleText() {
            return "";
        }

        @Override
        public float getAlignment() {
            return 5f;
        }

        @Nullable
        @Override
        public String getTooltipText() {
            return null;
        }

        @Nullable
        @Override
        public Consumer<MouseEvent> getClickConsumer() {
            return null;
        }

        @Override
        public StatusBarWidget copy() {
            return new MyEditorBasedWidget(myProject);
        }

        private void update(Editor editor) {
            if (editor.isDisposed()) {
                return;
            }

            myStatusBar.updateWidget(ID());
        }
    }
}

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

fun filePickerHandler(project: Project, referenceFile: VirtualFile): String? {
    var relativePath: String? = null

    ApplicationManager.getApplication().invokeAndWait {
        val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
        val selectedFile: VirtualFile? = FileChooser.chooseFile(descriptor, project, null)
        if (selectedFile != null) {
            relativePath = referenceFile.toNioPath().relativize(selectedFile.toNioPath()).toString()
        }
    }

    return relativePath
}

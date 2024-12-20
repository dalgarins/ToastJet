import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import java.nio.file.Files
import java.nio.file.StandardOpenOption

fun fileSaverHandler(project: Project, data: String): String? {
    var savedFilePath: String? = null

    ApplicationManager.getApplication().invokeAndWait {
        val descriptor = FileSaverDescriptor("Save As", "Select location to save the file")
        val dialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, project)
        val fileWrapper = dialog.save( "example.txt")

        fileWrapper?.file?.let { file ->
            try {
                Files.writeString(file.toPath(), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
                savedFilePath = file.path
                println("File saved at: ${file.path}")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error saving file: ${e.message}")
            }
        }
    }

    return savedFilePath
}

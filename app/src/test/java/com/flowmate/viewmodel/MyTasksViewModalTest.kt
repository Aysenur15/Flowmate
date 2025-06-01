
import com.flowmate.ui.component.TaskItem
import com.flowmate.viewmodel.MyTasksViewModal
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MyTasksViewModalTest {
    private lateinit var viewModel: MyTasksViewModal

    @Before
    fun setUp() {
        viewModel = MyTasksViewModal()
    }

    @Test
    fun testAddTask() {
        val task = TaskItem("3", "Test Task", "2023-10-03", false)
        viewModel.addTask(task)
        val tasks = viewModel.tasks.value
        assert(tasks.any { it.id == "3" && it.title == "Test Task" })
    }

    @Test
    fun testToggleTaskCompletion() {
        val taskId = viewModel.tasks.value.first().id
        val initial = viewModel.tasks.value.first { it.id == taskId }.isCompleted
        viewModel.toggleTaskCompletion(taskId)
        val updated = viewModel.tasks.value.first { it.id == taskId }.isCompleted
        assertEquals(!initial, updated)
    }
}


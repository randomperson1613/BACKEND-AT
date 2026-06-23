package ru.at.backend.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.at.backend.model.NoteCategory;
import ru.at.backend.model.request.CreateNoteRequest;
import ru.at.backend.model.request.UpdateCompletedRequest;
import ru.at.backend.model.request.UpdateNoteRequest;
import ru.at.backend.model.response.BaseResponse;
import ru.at.backend.model.response.ErrorResponse;
import ru.at.backend.model.response.Note;
import ru.at.backend.model.response.NoteResponse;
import ru.at.backend.model.response.NotesResponse;
import ru.at.backend.support.TestUser;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.at.backend.config.Specifications.responseSpec;

@Epic("Notes API")
@Feature("Notes")
@Owner("QA Automation")
class NotesTest extends BaseApiTest {

    @Test
    @Story("Создание заметки")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("POST /notes создает новую заметку")
    void shouldCreateNote() {
        TestUser user = registerAndLoginNewUser();
        CreateNoteRequest request = new CreateNoteRequest("Work note", "Prepare API diploma tests", NoteCategory.WORK);

        NoteResponse model = api.createNote(user.token(), request)
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(NoteResponse.class);
        Note note = model.data();

        assertThat(note.id()).isNotBlank();
        assertThat(note.title()).isEqualTo(request.title());
        assertThat(note.description()).isEqualTo(request.description());
        assertThat(note.category()).isEqualTo(request.category());
        assertThat(note.completed()).isFalse();
        assertThat(note.userId()).isEqualTo(user.id());
    }

    @Test
    @Story("Получение заметки")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /notes/{id} возвращает созданную заметку")
    void shouldReturnCreatedNoteById() {
        TestUser user = registerAndLoginNewUser();
        Note createdNote = createNote(user, "Personal note", "Read API docs", NoteCategory.PERSONAL);

        NoteResponse model = api.getNote(user.token(), createdNote.id())
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(NoteResponse.class);

        assertThat(model.data()).isEqualTo(createdNote);
    }

    @Test
    @Story("Получение списка заметок")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /notes возвращает заметки текущего пользователя")
    void shouldReturnAllNotesForCurrentUser() {
        TestUser user = registerAndLoginNewUser();
        Note homeNote = createNote(user, "Home note", "Buy milk", NoteCategory.HOME);
        Note workNote = createNote(user, "Second work note", "Review Jenkinsfile", NoteCategory.WORK);

        NotesResponse model = api.getNotes(user.token())
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(NotesResponse.class);
        List<String> noteIds = model.data().stream().map(Note::id).toList();

        assertThat(noteIds).contains(homeNote.id(), workNote.id());
        assertThat(model.data()).allSatisfy(note -> assertThat(note.userId()).isEqualTo(user.id()));
    }

    @Test
    @Story("Обновление заметки")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("PUT /notes/{id} обновляет title, description, completed и category")
    void shouldUpdateExistingNote() {
        TestUser user = registerAndLoginNewUser();
        Note createdNote = createNote(user, "Draft title", "Draft description", NoteCategory.HOME);
        UpdateNoteRequest request = new UpdateNoteRequest(
                "Updated title",
                "Updated description",
                true,
                NoteCategory.WORK
        );

        NoteResponse model = api.updateNote(user.token(), createdNote.id(), request)
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(NoteResponse.class);
        Note note = model.data();

        assertThat(note.id()).isEqualTo(createdNote.id());
        assertThat(note.title()).isEqualTo(request.title());
        assertThat(note.description()).isEqualTo(request.description());
        assertThat(note.completed()).isEqualTo(request.completed());
        assertThat(note.category()).isEqualTo(request.category());
        assertThat(note.updatedAt()).isNotEqualTo(createdNote.updatedAt());
    }

    @Test
    @Story("Обновление статуса заметки")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("PATCH /notes/{id} обновляет только completed")
    void shouldUpdateCompletedStatus() {
        TestUser user = registerAndLoginNewUser();
        Note createdNote = createNote(user, "Todo note", "Complete this task", NoteCategory.PERSONAL);

        NoteResponse model = api.updateCompleted(user.token(), createdNote.id(), new UpdateCompletedRequest(true))
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(NoteResponse.class);
        Note note = model.data();

        assertThat(note.id()).isEqualTo(createdNote.id());
        assertThat(note.title()).isEqualTo(createdNote.title());
        assertThat(note.completed()).isTrue();
        assertThat(note.description()).isEqualTo(createdNote.description());
    }

    @Test
    @Story("Удаление заметки")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("DELETE /notes/{id} удаляет заметку")
    void shouldDeleteNoteById() {
        TestUser user = registerAndLoginNewUser();
        Note createdNote = createNote(user, "Temporary note", "Delete me", NoteCategory.HOME);

        BaseResponse deleteModel = api.deleteNote(user.token(), createdNote.id())
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(BaseResponse.class);

        assertThat(deleteModel.message()).isEqualTo("Note successfully deleted");

        NotesResponse notesAfterDelete = api.getNotes(user.token())
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(NotesResponse.class);
        assertThat(notesAfterDelete.data()).extracting(Note::id).doesNotContain(createdNote.id());
    }

    @Test
    @Story("Негативное создание заметки")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("POST /notes без title возвращает 400")
    void shouldRejectNoteWithoutTitle() {
        TestUser user = registerAndLoginNewUser();
        CreateNoteRequest request = new CreateNoteRequest("", "Description without title", NoteCategory.WORK);

        ErrorResponse error = api.createNote(user.token(), request)
                .then()
                .spec(responseSpec(400, false))
                .extract()
                .as(ErrorResponse.class);

        assertThat(error.message()).containsIgnoringCase("title");
    }

    private Note createNote(TestUser user, String title, String description, NoteCategory category) {
        return api.createNote(user.token(), new CreateNoteRequest(title, description, category))
                .then()
                .spec(responseSpec(200, true))
                .extract()
                .as(NoteResponse.class)
                .data();
    }
}

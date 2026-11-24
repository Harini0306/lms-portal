import { useEffect, useState } from "react";
import {
  GraduationCap,
  Mail,
  Lock,
  User,
  BookOpen,
  LogOut,
  ArrowRight,
  ShieldCheck,
  ExternalLink,
  CheckCircle2,
  PlayCircle,
  Plus,
  FileText,
  Trash2,
  Video,
  Link as LinkIcon,
  Settings,
} from "lucide-react";

import {
  loginUser,
  registerStudent,
  logoutUser,
} from "./services/authService";
import api from "./services/api";
import "./App.css";

function App() {
  const [page, setPage] = useState("login");
  const [user, setUser] = useState(() => {
    const token = localStorage.getItem("token");

    if (!token) return null;

    return {
      email: localStorage.getItem("email"),
      role: localStorage.getItem("role"),
    };
  });

  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const [courses, setCourses] = useState([]);
  const [enrollments, setEnrollments] = useState([]);
  const [courseMessage, setCourseMessage] = useState("");
  const [activeTab, setActiveTab] = useState("all");

  const [selectedCourse, setSelectedCourse] = useState(null);
  const [materials, setMaterials] = useState([]);
  const [progress, setProgress] = useState(null);
  const [materialMessage, setMaterialMessage] = useState("");
  const [contentLoading, setContentLoading] = useState(false);
  const [completingMaterialId, setCompletingMaterialId] = useState(null);

  // Admin dashboard state
  const [courseForm, setCourseForm] = useState({
    title: "",
    description: "",
    instructor: "",
    durationHours: "",
    published: true,
  });
  const [materialForm, setMaterialForm] = useState({
    title: "",
    description: "",
    type: "PDF",
    url: "",
  });
  const [adminSelectedCourseId, setAdminSelectedCourseId] = useState("");
  const [adminMaterials, setAdminMaterials] = useState([]);
  const [adminMessage, setAdminMessage] = useState("");
  const [adminError, setAdminError] = useState("");
  const [adminLoading, setAdminLoading] = useState(false);

  const loadCourses = async () => {
    try {
      const response = await api.get("/api/courses");
      setCourses(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error("Could not load courses:", err);
      setCourses([]);
    }
  };

  const loadEnrollments = async () => {
    if (localStorage.getItem("role") !== "STUDENT") {
      setEnrollments([]);
      return;
    }

    try {
      const response = await api.get("/api/enrollments/me");
      setEnrollments(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error("Could not load enrollments:", err);
      setEnrollments([]);
    }
  };

  useEffect(() => {
    if (user) {
      loadCourses();
      loadEnrollments();
    }
    // Load dashboard data when an existing login is restored.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleChange = (event) => {
    setForm((currentForm) => ({
      ...currentForm,
      [event.target.name]: event.target.value,
    }));
  };

  const handleRegister = async (event) => {
    event.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);

    try {
      await registerStudent(form);
      setMessage("Registration successful! Please log in.");
      setForm({ name: "", email: "", password: "" });
      setPage("login");
    } catch (err) {
      const responseMessage =
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data;

      setError(
        typeof responseMessage === "string"
          ? responseMessage
          : "Registration failed. Please check your details."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (event) => {
    event.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);

    try {
      const data = await loginUser({
        email: form.email,
        password: form.password,
      });

      const loggedInUser = {
        email: data.email,
        role: data.role,
      };

      setUser(loggedInUser);
      setForm({ name: "", email: "", password: "" });
      setActiveTab("all");
      await Promise.all([loadCourses(), loadEnrollments()]);
    } catch (err) {
      const responseMessage =
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data;

      setError(
        typeof responseMessage === "string"
          ? responseMessage
          : "Login failed. Please check your email and password."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleEnroll = async (courseId) => {
    setCourseMessage("");

    try {
      await api.post(
        `/api/enrollments?courseId=${encodeURIComponent(courseId)}`
      );

      setCourseMessage("Successfully enrolled in the course!");
      await loadEnrollments();
    } catch (err) {
      if (err.response?.status === 409) {
        setCourseMessage("You are already enrolled in this course.");
        await loadEnrollments();
      } else {
        setCourseMessage("Enrollment failed. Please try again.");
      }
    }
  };

  const loadCourseContent = async (course) => {
    setSelectedCourse(course);
    setMaterials([]);
    setProgress(null);
    setMaterialMessage("");
    setContentLoading(true);

    try {
      const [materialsResponse, progressResponse] = await Promise.all([
        api.get(`/api/materials/course/${course.id}`),
        api
          .get(`/api/progress/me/course/${course.id}`)
          .catch((err) => {
            if (err.response?.status === 404) {
              return { data: null };
            }
            throw err;
          }),
      ]);

      setMaterials(
        Array.isArray(materialsResponse.data) ? materialsResponse.data : []
      );
      setProgress(progressResponse.data);
    } catch (err) {
      console.error("Could not load course content:", err);
      setMaterialMessage(
        "Could not load this course's content. Please try again."
      );
    } finally {
      setContentLoading(false);
    }
  };

  const handleCompleteMaterial = async (materialId) => {
    if (!selectedCourse) return;

    setCompletingMaterialId(materialId);
    setMaterialMessage("");

    try {
      await api.post(
        `/api/progress/complete?courseId=${encodeURIComponent(
          selectedCourse.id
        )}&materialId=${encodeURIComponent(materialId)}`
      );

      const response = await api.get(
        `/api/progress/me/course/${selectedCourse.id}`
      );

      setProgress(response.data);
      setMaterialMessage("Lesson marked as completed!");
    } catch (err) {
      console.error("Could not update lesson progress:", err);
      setMaterialMessage(
        err.response?.status === 409
          ? "This lesson is already completed."
          : "Could not update progress. Please try again."
      );
    } finally {
      setCompletingMaterialId(null);
    }
  };

  const loadAdminMaterials = async (courseId) => {
    if (!courseId) {
      setAdminMaterials([]);
      return;
    }

    try {
      const response = await api.get(`/api/materials/course/${courseId}`);
      setAdminMaterials(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error("Could not load admin materials:", err);
      setAdminMaterials([]);
      setAdminError("Could not load materials for this course.");
    }
  };

  const handleCreateCourse = async (event) => {
    event.preventDefault();
    setAdminMessage("");
    setAdminError("");
    setAdminLoading(true);

    try {
      const response = await api.post("/api/courses", {
        title: courseForm.title.trim(),
        description: courseForm.description.trim(),
        instructor: courseForm.instructor.trim(),
        durationHours: Number(courseForm.durationHours),
        published: Boolean(courseForm.published),
      });

      setAdminMessage("Course created successfully.");
      setCourseForm({
        title: "",
        description: "",
        instructor: "",
        durationHours: "",
        published: true,
      });
      await loadCourses();

      if (response.data?.id) {
        setAdminSelectedCourseId(response.data.id);
      }
    } catch (err) {
      console.error("Could not create course:", err);
      setAdminError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Could not create course. Check the details and try again."
      );
    } finally {
      setAdminLoading(false);
    }
  };

  const handleCreateMaterial = async (event) => {
    event.preventDefault();
    setAdminMessage("");
    setAdminError("");

    if (!adminSelectedCourseId) {
      setAdminError("Select a course before adding learning material.");
      return;
    }

    setAdminLoading(true);

    try {
      await api.post("/api/materials", {
        courseId: adminSelectedCourseId,
        title: materialForm.title.trim(),
        description: materialForm.description.trim(),
        type: materialForm.type,
        url: materialForm.url.trim(),
      });

      setAdminMessage("Learning material added successfully.");
      setMaterialForm({
        title: "",
        description: "",
        type: "PDF",
        url: "",
      });
      await loadAdminMaterials(adminSelectedCourseId);
    } catch (err) {
      console.error("Could not add learning material:", err);
      setAdminError(
        err.response?.data?.message ||
        err.response?.data?.error ||
        "Could not add material. Check the details and try again."
      );
    } finally {
      setAdminLoading(false);
    }
  };

  const handleDeleteMaterial = async (materialId) => {
    const confirmed = window.confirm(
      "Delete this learning material? This action cannot be undone."
    );
    if (!confirmed) return;

    setAdminMessage("");
    setAdminError("");

    try {
      await api.delete(`/api/materials/${materialId}`);
      setAdminMessage("Learning material deleted.");
      await loadAdminMaterials(adminSelectedCourseId);
    } catch (err) {
      console.error("Could not delete material:", err);
      setAdminError("Could not delete this material. Please try again.");
    }
  };

  const handleLogout = () => {
    logoutUser();
    setUser(null);
    setPage("login");
    setCourses([]);
    setEnrollments([]);
    setSelectedCourse(null);
    setMaterials([]);
    setProgress(null);
    setCourseMessage("");
    setMaterialMessage("");
    setActiveTab("all");
    setError("");
    setMessage("");
  };

  const closeCourse = () => {
    setSelectedCourse(null);
    setMaterials([]);
    setProgress(null);
    setMaterialMessage("");
  };

  if (user) {
    const enrolledCourseIds = enrollments.map((item) => item.courseId);
    const visibleCourses = courses
      .filter((course) => user.role !== "STUDENT" || course.published)
      .filter(
        (course) =>
          user.role !== "STUDENT" ||
          activeTab === "all" ||
          enrolledCourseIds.includes(course.id)
      );

    const completedMaterialIds = progress?.completedMaterialIds || [];
    const completionPercentage = Number(
      progress?.completionPercentage || 0
    );

    return (
      <div className="auth-dashboard">
        <header className="dashboard-header">
          <div className="auth-brand">
            <div className="auth-brand-icon">
              <GraduationCap />
            </div>
            <span>LearnSpace</span>
          </div>

          <button className="logout-button" onClick={handleLogout}>
            <LogOut size={17} />
            Logout
          </button>
        </header>

        <main className="dashboard-welcome">
          <div className="dashboard-greeting">
            <span className="welcome-tag">YOUR LEARNING SPACE</span>
            <h1>Welcome back!</h1>
            <p>
              Continue learning and build your skills one lesson at a time.
            </p>
          </div>

          <div className="user-summary">
            <div className="user-avatar">
              {user.email?.charAt(0).toUpperCase()}
            </div>
            <div>
              <strong>{user.email}</strong>
              <span>{user.role}</span>
            </div>
            <ShieldCheck className="verified-icon" />
          </div>

          <section className="dashboard-stats">
            <div className="dashboard-stat">
              <BookOpen />
              <span>Available courses</span>
              <strong>{courses.filter((course) => course.published).length}</strong>
            </div>
            <div className="dashboard-stat">
              <GraduationCap />
              <span>
                {user.role === "STUDENT" ? "My enrollments" : "Account role"}
              </span>
              <strong>
                {user.role === "STUDENT" ? enrollments.length : user.role}
              </strong>
            </div>
          </section>

          {user.role === "ADMIN" && (
            <section className="admin-workspace">
              <div className="admin-heading">
                <div>
                  <span className="welcome-tag">ADMINISTRATION</span>
                  <h2>Admin Dashboard</h2>
                  <p>Create courses and add learning resources for students.</p>
                </div>
                <div className="admin-heading-icon"><Settings size={25} /></div>
              </div>

              {adminMessage && <div className="success-message">{adminMessage}</div>}
              {adminError && <div className="error-message">{adminError}</div>}

              <div className="admin-panels">
                <section className="admin-panel">
                  <div className="admin-panel-title">
                    <Plus size={19} />
                    <div>
                      <h3>Create a Course</h3>
                      <p>Set up a course and choose whether to publish it.</p>
                    </div>
                  </div>

                  <form className="admin-form" onSubmit={handleCreateCourse}>
                    <label className="admin-label">
                      Course title
                      <input
                        value={courseForm.title}
                        onChange={(event) => setCourseForm({ ...courseForm, title: event.target.value })}
                        placeholder="e.g. Java Fundamentals"
                        required
                      />
                    </label>

                    <label className="admin-label">
                      Description
                      <textarea
                        value={courseForm.description}
                        onChange={(event) => setCourseForm({ ...courseForm, description: event.target.value })}
                        placeholder="What will students learn?"
                        rows="3"
                        required
                      />
                    </label>

                    <div className="admin-form-row">
                      <label className="admin-label">
                        Instructor
                        <input
                          value={courseForm.instructor}
                          onChange={(event) => setCourseForm({ ...courseForm, instructor: event.target.value })}
                          placeholder="Instructor name"
                          required
                        />
                      </label>

                      <label className="admin-label">
                        Duration (hours)
                        <input
                          type="number"
                          min="1"
                          value={courseForm.durationHours}
                          onChange={(event) => setCourseForm({ ...courseForm, durationHours: event.target.value })}
                          placeholder="e.g. 12"
                          required
                        />
                      </label>
                    </div>

                    <label className="admin-checkbox">
                      <input
                        type="checkbox"
                        checked={courseForm.published}
                        onChange={(event) => setCourseForm({ ...courseForm, published: event.target.checked })}
                      />
                      Publish this course immediately
                    </label>

                    <button className="primary-button admin-submit" type="submit" disabled={adminLoading}>
                      <Plus size={16} />
                      {adminLoading ? "Saving..." : "Create Course"}
                    </button>
                  </form>
                </section>

                <section className="admin-panel">
                  <div className="admin-panel-title">
                    <FileText size={19} />
                    <div>
                      <h3>Add Learning Material</h3>
                      <p>Attach a PDF, video, or document link to a course.</p>
                    </div>
                  </div>

                  <form className="admin-form" onSubmit={handleCreateMaterial}>
                    <label className="admin-label">
                      Select course
                      <select
                        value={adminSelectedCourseId}
                        onChange={(event) => {
                          setAdminSelectedCourseId(event.target.value);
                          setAdminMessage("");
                          setAdminError("");
                          loadAdminMaterials(event.target.value);
                        }}
                        required
                      >
                        <option value="">Choose a course</option>
                        {courses.map((course) => (
                          <option value={course.id} key={course.id}>{course.title}</option>
                        ))}
                      </select>
                    </label>

                    <label className="admin-label">
                      Material title
                      <input
                        value={materialForm.title}
                        onChange={(event) => setMaterialForm({ ...materialForm, title: event.target.value })}
                        placeholder="e.g. Introduction to Java PDF"
                        required
                      />
                    </label>

                    <label className="admin-label">
                      Description
                      <textarea
                        value={materialForm.description}
                        onChange={(event) => setMaterialForm({ ...materialForm, description: event.target.value })}
                        placeholder="Briefly describe this lesson or resource"
                        rows="2"
                        required
                      />
                    </label>

                    <div className="admin-form-row">
                      <label className="admin-label">
                        Material type
                        <select
                          value={materialForm.type}
                          onChange={(event) => setMaterialForm({ ...materialForm, type: event.target.value })}
                        >
                          <option value="PDF">PDF</option>
                          <option value="VIDEO">Video</option>
                          <option value="DOCUMENT">Document</option>
                          <option value="LINK">External link</option>
                        </select>
                      </label>

                      <label className="admin-label">
                        Resource URL
                        <input
                          type="url"
                          value={materialForm.url}
                          onChange={(event) => setMaterialForm({ ...materialForm, url: event.target.value })}
                          placeholder="https://..."
                          required
                        />
                      </label>
                    </div>

                    <button className="primary-button admin-submit" type="submit" disabled={adminLoading || !adminSelectedCourseId}>
                      <Plus size={16} />
                      {adminLoading ? "Saving..." : "Add Material"}
                    </button>
                  </form>

                  <div className="admin-material-list">
                    <h4>Materials in selected course</h4>
                    {!adminSelectedCourseId ? (
                      <p className="admin-muted">Choose a course to view its materials.</p>
                    ) : adminMaterials.length === 0 ? (
                      <p className="admin-muted">No materials added to this course yet.</p>
                    ) : (
                      adminMaterials.map((material) => (
                        <div className="admin-material-row" key={material.id}>
                          <div className="admin-material-icon">
                            {material.type === "VIDEO" ? <Video size={17} /> : material.type === "LINK" ? <LinkIcon size={17} /> : <FileText size={17} />}
                          </div>
                          <div className="admin-material-details">
                            <strong>{material.title}</strong>
                            <span>{material.type}</span>
                          </div>
                          <button
                            type="button"
                            className="admin-delete-button"
                            title="Delete material"
                            onClick={() => handleDeleteMaterial(material.id)}
                          >
                            <Trash2 size={16} />
                          </button>
                        </div>
                      ))
                    )}
                  </div>
                </section>
              </div>

              <section className="admin-course-list">
                <div className="admin-course-list-heading">
                  <div>
                    <h3>Course Management</h3>
                    <p>{courses.length} course(s) in the portal</p>
                  </div>
                  <button className="refresh-button" onClick={loadCourses}>Refresh</button>
                </div>
                {courses.length === 0 ? (
                  <p className="admin-muted">No courses created yet.</p>
                ) : (
                  <div className="admin-course-table">
                    {courses.map((course) => (
                      <div className="admin-course-row" key={course.id}>
                        <div>
                          <strong>{course.title}</strong>
                          <span>{course.instructor} · {course.durationHours} hours</span>
                        </div>
                        <span className={course.published ? "admin-status published" : "admin-status draft"}>
                          {course.published ? "Published" : "Draft"}
                        </span>
                        <button
                          className="admin-select-button"
                          onClick={() => {
                            setAdminSelectedCourseId(course.id);
                            loadAdminMaterials(course.id);
                            document.querySelector(".admin-workspace")?.scrollIntoView({ behavior: "smooth" });
                          }}
                        >
                          Add materials
                        </button>
                      </div>
                    ))}
                  </div>
                )}
              </section>
            </section>
          )}

          <section className="real-courses">
            <div className="real-courses-heading">
              <div>
                <span className="welcome-tag">START LEARNING</span>
                <h2>Available Courses</h2>
              </div>

              <button
                className="refresh-button"
                onClick={() => {
                  loadCourses();
                  loadEnrollments();
                }}
              >
                Refresh courses
              </button>
            </div>

            {courseMessage && (
              <div className="success-message">{courseMessage}</div>
            )}

            {user.role === "STUDENT" && (
              <div className="auth-tabs course-tabs">
                <button
                  className={activeTab === "all" ? "selected" : ""}
                  onClick={() => setActiveTab("all")}
                >
                  All Courses
                </button>
                <button
                  className={activeTab === "mine" ? "selected" : ""}
                  onClick={() => setActiveTab("mine")}
                >
                  My Enrollments ({enrollments.length})
                </button>
              </div>
            )}

            {courses.length === 0 ? (
              <div className="empty-courses">
                <BookOpen size={35} />
                <h3>No courses available yet</h3>
                <p>
                  Courses added by your administrator will appear here.
                </p>
              </div>
            ) : visibleCourses.length === 0 ? (
              <div className="empty-courses">
                <BookOpen size={35} />
                <h3>
                  {activeTab === "mine"
                    ? "No enrollments yet"
                    : "No published courses"}
                </h3>
                <p>
                  {activeTab === "mine"
                    ? "Enroll in a course to see it here."
                    : "Published courses will appear here."}
                </p>
              </div>
            ) : (
              <div className="real-course-grid">
                {visibleCourses.map((course) => {
                  const enrolled = enrolledCourseIds.includes(course.id);

                  return (
                    <article className="real-course-card" key={course.id}>
                      <div className="real-course-icon">
                        <BookOpen size={26} />
                      </div>

                      <h3>{course.title}</h3>
                      <p>{course.description}</p>

                      <span>Instructor: {course.instructor}</span>
                      <span>Duration: {course.durationHours} hours</span>

                      {user.role === "STUDENT" && (
                        <>
                          <button
                            className="primary-button"
                            disabled={enrolled}
                            onClick={() => handleEnroll(course.id)}
                          >
                            {enrolled ? "Already Enrolled" : "Enroll Now"}
                          </button>

                          {enrolled && (
                            <button
                              className="secondary-button"
                              onClick={() => loadCourseContent(course)}
                            >
                              <PlayCircle size={15} />
                              Open Course
                            </button>
                          )}
                        </>
                      )}

                      {user.role !== "STUDENT" && (
                        <span className="published-status">
                          {course.published ? "Published" : "Draft"}
                        </span>
                      )}
                    </article>
                  );
                })}
              </div>
            )}
          </section>

          {selectedCourse && (
            <section className="real-courses learning-section">
              <div className="real-courses-heading">
                <div>
                  <span className="welcome-tag">YOUR LEARNING SPACE</span>
                  <h2>{selectedCourse.title}</h2>
                  <p className="course-description">
                    {selectedCourse.description}
                  </p>
                </div>

                <button className="refresh-button" onClick={closeCourse}>
                  Back to Courses
                </button>
              </div>

              {contentLoading ? (
                <div className="empty-courses">
                  <p>Loading course materials...</p>
                </div>
              ) : (
                <>
                  <div className="progress-card">
                    <div className="progress-card-heading">
                      <div>
                        <h3>Your Course Progress</h3>
                        <p>
                          {completedMaterialIds.length} of {materials.length}{" "}
                          lessons completed
                        </p>
                      </div>
                      <strong>{completionPercentage}%</strong>
                    </div>

                    <div
                      className="progress-track"
                      role="progressbar"
                      aria-valuenow={completionPercentage}
                      aria-valuemin="0"
                      aria-valuemax="100"
                    >
                      <div
                        className="progress-fill"
                        style={{
                          width: `${Math.min(
                            100,
                            Math.max(0, completionPercentage)
                          )}%`,
                        }}
                      />
                    </div>
                  </div>

                  {materialMessage && (
                    <div className="success-message">{materialMessage}</div>
                  )}

                  <h3 className="materials-heading">Course Materials</h3>

                  {materials.length === 0 ? (
                    <div className="empty-courses">
                      <BookOpen size={32} />
                      <h3>No learning materials yet</h3>
                      <p>
                        Your instructor has not added materials to this course.
                      </p>
                    </div>
                  ) : (
                    <div className="real-course-grid">
                      {materials.map((material) => {
                        const completed = completedMaterialIds.includes(
                          material.id
                        );

                        return (
                          <article
                            className="real-course-card material-card"
                            key={material.id}
                          >
                            <div className="real-course-icon">
                              {completed ? (
                                <CheckCircle2 size={25} />
                              ) : (
                                <BookOpen size={25} />
                              )}
                            </div>

                            <h3>{material.title}</h3>
                            <p>{material.description}</p>
                            <span>Type: {material.type}</span>

                            {material.url && (
                              <a
                                href={material.url}
                                target="_blank"
                                rel="noreferrer"
                                className="material-link"
                              >
                                Open Learning Resource
                                <ExternalLink size={14} />
                              </a>
                            )}

                            <button
                              className="primary-button"
                              disabled={
                                completed ||
                                completingMaterialId === material.id
                              }
                              onClick={() =>
                                handleCompleteMaterial(material.id)
                              }
                            >
                              {completed
                                ? "Completed"
                                : completingMaterialId === material.id
                                  ? "Updating..."
                                  : "Mark as Completed"}
                            </button>
                          </article>
                        );
                      })}
                    </div>
                  )}
                </>
              )}
            </section>
          )}
        </main>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <section className="auth-intro">
        <div className="auth-brand">
          <div className="auth-brand-icon">
            <GraduationCap />
          </div>
          <span>LearnSpace</span>
        </div>

        <div className="intro-content">
          <span className="welcome-tag">LEARN. GROW. ACHIEVE.</span>
          <h1>
            Your next chapter
            <br />
            starts with learning.
          </h1>
          <p>
            Discover courses, access learning materials, track your progress
            and take control of your learning journey.
          </p>

          <div className="intro-feature">
            <div>
              <BookOpen />
            </div>
            <span>Learn at your own pace</span>
          </div>
          <div className="intro-feature">
            <div>
              <ShieldCheck />
            </div>
            <span>Secure student accounts</span>
          </div>
        </div>

        <p className="intro-footer">© 2026 LearnSpace Learning Portal</p>
      </section>

      <section className="auth-form-section">
        <div className="auth-form-container">
          <div className="mobile-auth-brand">
            <div className="auth-brand-icon">
              <GraduationCap />
            </div>
            <span>LearnSpace</span>
          </div>

          <span className="welcome-tag">
            {page === "login" ? "WELCOME BACK" : "GET STARTED"}
          </span>

          <h2>
            {page === "login"
              ? "Sign in to your account"
              : "Create your account"}
          </h2>
          <p className="auth-description">
            {page === "login"
              ? "Enter your details to continue your learning journey."
              : "Join LearnSpace and start learning today."}
          </p>

          <div className="auth-tabs">
            <button
              className={page === "login" ? "selected" : ""}
              onClick={() => {
                setPage("login");
                setError("");
                setMessage("");
              }}
            >
              Login
            </button>
            <button
              className={page === "register" ? "selected" : ""}
              onClick={() => {
                setPage("register");
                setError("");
                setMessage("");
              }}
            >
              Register
            </button>
          </div>

          {message && <div className="success-message">{message}</div>}
          {error && <div className="error-message">{error}</div>}

          <form onSubmit={page === "login" ? handleLogin : handleRegister}>
            {page === "register" && (
              <label className="auth-label">
                Full name
                <div className="auth-input">
                  <User size={18} />
                  <input
                    name="name"
                    placeholder="Enter your full name"
                    value={form.name}
                    onChange={handleChange}
                    required
                  />
                </div>
              </label>
            )}

            <label className="auth-label">
              Email address
              <div className="auth-input">
                <Mail size={18} />
                <input
                  type="email"
                  name="email"
                  placeholder="you@example.com"
                  value={form.email}
                  onChange={handleChange}
                  required
                />
              </div>
            </label>

            <label className="auth-label">
              Password
              <div className="auth-input">
                <Lock size={18} />
                <input
                  type="password"
                  name="password"
                  placeholder="Enter your password"
                  value={form.password}
                  onChange={handleChange}
                  minLength={6}
                  required
                />
              </div>
            </label>

            <button
              className="auth-submit"
              type="submit"
              disabled={loading}
            >
              {loading
                ? "Please wait..."
                : page === "login"
                  ? "Sign in"
                  : "Create account"}
              {!loading && <ArrowRight size={18} />}
            </button>
          </form>

          <p className="auth-switch">
            {page === "login"
              ? "Don't have an account?"
              : "Already registered?"}
            <button
              onClick={() => {
                setPage(page === "login" ? "register" : "login");
                setError("");
                setMessage("");
              }}
            >
              {page === "login" ? "Create account" : "Sign in"}
            </button>
          </p>

          <p className="security-note">
            <ShieldCheck size={15} />
            Your account is protected with secure authentication.
          </p>
        </div>
      </section>
    </div>
  );
}

export default App;

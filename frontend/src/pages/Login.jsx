import { useTranslation } from "react-i18next";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import Page from "../components/Page.jsx";
import "./styles/login.styles.css";
import {useContext, useEffect, useState} from "react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import {EmailSchema, LoginSchema} from "../data/validation.js";
import {useApi} from "../hooks/useApi.js";
import ApiContext from "../contexts/ApiContext.jsx";
import {useMutation} from "@tanstack/react-query";
import {useUserService} from "../hooks/services/useUserService.js";
import AuthContext from "../contexts/AuthContext.jsx";

function Login() {
    const { t } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const userService = useUserService(api);
    const navigate = useNavigate();

    const [forgotPassword, setForgotPassword] = useState(false);
    const forgotPasswordMutation = useMutation({
        mutationFn: async (email) => {
            await userService.sendResetPasswordToken(
                apiContext.usersUrl,
                email
            );
        }
    });

    const [queryParams, setQueryParams] = useSearchParams();
    const [alertType, setAlertType] = useState("");
    const [alertMessage, setAlertMessage] = useState("");

    const handleSendEmail = (values, {setSubmitting}) => {
        forgotPasswordMutation.mutate(
            values.email,
            {
                onSuccess: () => {
                    setAlertType("success");
                    setAlertMessage("login.reset_password_email_sent");
                },
                onError: () => {
                    setAlertType("danger");
                    setAlertMessage("login.mailer_error");
                }
            }
        );
        setSubmitting(false);
        setForgotPassword(false);
    };

    const handleLogin = async (values, {setSubmitting}) => {
        const success = await authContext.login(values.email, values.password, values.rememberme);
        setSubmitting(false);
        if (success) {
            navigate(queryParams.get("next") || "/");
        } else {
            setAlertType("danger");
            setAlertMessage("login.login_error");
        }
    };

    useEffect(() => {
        if (authContext.isAuthenticated) {
            navigate(queryParams.get("next") || "/");
        }

        if (queryParams.has("alertType")) {
            setAlertType(queryParams.get("alertType"));
            queryParams.delete("alertType");
        }
        if (queryParams.has("alertMessage")) {
            setAlertMessage(queryParams.get("alertMessage"));
            queryParams.delete("alertMessage");
        }
        setQueryParams(queryParams, {replace: true});
    }, [authContext.isAuthenticated, navigate, queryParams, setQueryParams]);

    return (
        <>
            <Page title={t("titles.login")} className="login">
                {!forgotPassword &&
                    <div className="card">
                        <div className="card-body">
                            {alertType !== "" && alertMessage !== "" &&
                                <div className={`alert alert-${alertType}`} role="alert">
                                    {t(alertMessage)}
                                </div>
                            }
                            <h2 className="card-title mb-3">{t("titles.login")}</h2>
                            <Formik
                                initialValues={{
                                    email: "",
                                    password: "",
                                    rememberme: false
                                }}
                                validationSchema={LoginSchema}
                                onSubmit={handleLogin}
                            >
                                {({ isSubmitting }) => (
                                    <Form>
                                        <div className="mb-3">
                                            <label htmlFor="email" className="form-label">{t("login.email_address")}</label>
                                            <Field type="email" className="form-control" name="email" autoComplete="email" id="email"/>
                                            <ErrorMessage name="email" className="form-error" component="div"/>
                                        </div>
                                        <div className="mb-3">
                                            <label htmlFor="password" className="form-label">{t("login.password")}</label>
                                            <Field type="password" className="form-control" name="password" autoComplete="current-password" id="password"/>
                                            <ErrorMessage name="password" className="form-error" component="div"/>
                                        </div>
                                        <div className="mb-3 form-check">
                                            <Field type="checkbox" className="form-check-input" name="rememberme" id="rememberme"/>
                                            <label className="form-check-label" htmlFor="rememberme">{t("login.remember_me")}</label>
                                        </div>
                                        <button className="btn btn-primary" type="submit" disabled={isSubmitting}>{t("login.login_button")}</button>
                                    </Form>
                                )}
                            </Formik>
                            <p className="mt-3">{t("login.no_account")} <Link to="/auth/register">{t("login.signup_here")}</Link></p>
                            <span className="mt-3 d-flex align-items-center justify-content-center more-actions">
                                <button type="button" className="btn btn-link" onClick={() => setForgotPassword(true)}>{t("login.forgot_password")}</button>
                            </span>
                        </div>
                    </div>
                }
                {forgotPassword &&
                    <div className="card">
                        <div className="card-body">
                            <h2 className="card-title mb-3">{t("login.forgot_password")}</h2>
                            <Formik
                                initialValues={{
                                    email: ""
                                }}
                                onSubmit={handleSendEmail}
                                validationSchema={EmailSchema}
                            >
                                {({ isSubmitting }) => (
                                    <Form>
                                        <div className="mb-3">
                                            <label htmlFor="email" className="form-label">{t("login.email_address")}</label>
                                            <Field type="email" className="form-control" name="email" id="email"/>
                                            <ErrorMessage name="email" className="form-error" component="div"/>
                                        </div>
                                        <button className="btn btn-primary mb-2" type="submit" disabled={isSubmitting}>{t("login.send_email")}</button>
                                    </Form>
                                )}
                            </Formik>
                            <button className="btn btn-secondary" onClick={() => {
                                setForgotPassword(false);
                            }}>{t("login.go_back")}</button>
                        </div>
                    </div>
                }
            </Page>
        </>
    );
}

export default Login;

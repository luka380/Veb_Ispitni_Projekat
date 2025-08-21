import React, {useState} from "react";
import {api, BASE, getUserInfo, jsonHeaders} from "../api.ts";
import {Button} from "./Button.tsx";
import {Modal} from "./Modal.tsx";
import {Input} from "./SmallComponents.tsx";
import {useApp} from "../AppContext.tsx";
import {UserType} from "../models.ts";

export function LoginBox() {
    const {state, dispatch} = useApp();
    const [open, setOpen] = useState(false);
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPw, setShowPw] = useState(false);
    const [err, setErr] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleLogin(e: React.FormEvent) {
        e.preventDefault();
        setErr("");
        setLoading(true);
        try {
            const res = await fetch(BASE + "auth/login", {
                method: "POST",
                credentials: "include",
                headers: jsonHeaders,
                body: JSON.stringify({email, password}),
            });
            if (!res.ok) throw new Error(await res.text());
            setOpen(false);
            setEmail("");
            setPassword("");
            dispatch({type: "setAccount", payload: await getUserInfo()});
        } catch (e: any) {
            setErr(e?.message || String(e));
        } finally {
            setLoading(false);
        }
    }

    async function handleLogout() {
        setErr("");
        setLoading(true);
        try {
            await api("auth/logout", {method: "POST"});
            dispatch({type: "setAccount", payload: await getUserInfo()});
        } catch (e: any) {
            setErr(e?.message || String(e));
        } finally {
            setLoading(false);
        }
    }

    if (state.currentAccount?.userType === UserType.ANONYMOUS) {
        return (
            <div className="flex items-center gap-2">
                <Button variant="primary" size="sm" onClick={() => setOpen(true)} aria-haspopup>
                    Prijava
                </Button>
                {open && (
                    <Modal open={open} onClose={() => setOpen(false)} center>
                        <div
                            className="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl my-8 max-h-[92svh] md:max-h-[90vh] overflow-y-auto">
                            <div className="mb-4 flex items-center justify-between">
                                <h3 className="text-lg font-semibold">Prijava</h3>
                                <Button variant="ghost" size="sm" onClick={() => setOpen(false)}>
                                    Zatvori
                                </Button>
                            </div>

                            {err && (
                                <div
                                    className="mb-3 rounded-xl border border-red-200 bg-red-50 p-2 text-sm text-red-700">
                                    {err}
                                </div>
                            )}

                            <form className="grid gap-3" onSubmit={handleLogin}>
                                <Input
                                    label="Email"
                                    type="email"
                                    autoComplete="email"
                                    placeholder="you@example.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                                <div className="grid gap-1">
                                    <div className="flex items-center justify-between">
                                        <span className="text-sm font-medium text-gray-700">Lozinka</span>
                                        <button
                                            type="button"
                                            onClick={() => setShowPw((s) => !s)}
                                            className="text-xs text-gray-600 hover:underline"
                                        >
                                            {showPw ? "Sakrij" : "Prikaži"}
                                        </button>
                                    </div>
                                    <input
                                        className="w-full h-11 rounded-xl border border-gray-200 bg-white px-3 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-black/20"
                                        type={showPw ? "text" : "password"}
                                        autoComplete="current-password"
                                        placeholder="••••••••"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required
                                    />
                                </div>
                                <Button variant="primary" disabled={loading}>
                                    {loading ? "Prijavljivanje…" : "Prijava"}
                                </Button>
                            </form>
                        </div>
                    </Modal>
                )}
            </div>
        );
    }

    return (
        <div className="flex items-center gap-3 text-sm">
            <span className="text-gray-700">
                Ulogovan: <b>{state.currentAccount?.userType}</b>
            </span>
            <Button variant="soft" size="sm" disabled={loading} onClick={handleLogout}>
                Odjava
            </Button>
            {err && <span className="text-sm text-red-600 ml-2">{err}</span>}
        </div>
    );
}
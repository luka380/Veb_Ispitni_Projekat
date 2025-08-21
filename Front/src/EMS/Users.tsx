import {api, jsonHeaders, queryParams, usePaged} from "../api.ts";
import type {UserDTO, UserType} from "../models.ts";
import {useState} from "react";
import {Input, Section} from "../Components/SmallComponents.tsx";
import {Button} from "../Components/Button.tsx";
import {Pagination} from "../Components/Pagination.tsx";

export function EMSUsers() {
    const [refreshTick, setRefreshTick] = useState(0);
    const {data, loading, error, page, setPage, size} = usePaged<UserDTO>(
        ({page, size}) => api(`users${queryParams({page, size})}`),
        [refreshTick]
    );

    const [form, setForm] = useState<{
        firstName: string;
        lastName: string;
        email: string;
        password: string;
        password2: string;
        type: Exclude<UserType, "ANONYMOUS">;
    }>({firstName: "", lastName: "", email: "", password: "", password2: "", type: "EVENT_CREATOR"});

    async function add() {
        if (form.password !== form.password2) return alert("Lozinke se ne poklapaju.");
        const payload = {
            firstName: form.firstName,
            lastName: form.lastName,
            email: form.email,
            userType: form.type,
            password: form.password,
        };
        try {
            await api("users", {method: "POST", headers: jsonHeaders, body: JSON.stringify(payload)});
            setRefreshTick((t) => t + 1);
            setForm({firstName: "", lastName: "", email: "", password: "", password2: "", type: "EVENT_CREATOR"});
        } catch (e: any) {
            alert(e?.message || String(e));
        }
    }

    async function activate(u: UserDTO, active: boolean) {
        try {
            await api(`users/${u.id}/${active ? "activate" : "deactivate"}`, {method: "POST"});
            setRefreshTick((t) => t + 1);
        } catch (e: any) {
            alert(e?.message || String(e));
        }
    }

    async function del(u: UserDTO) {
        if (!window.confirm("Obrisati korisnika?")) return;
        try {
            await api(`users/${u.id}`, {method: "DELETE"});
            setRefreshTick((t) => t + 1);
        } catch (e: any) {
            alert(e?.message || String(e));
        }
    }

    const table = Array.isArray((data as any).items) ? (data as any) : {items: (data as any)};

    const items = Array.isArray((data as any).items)
        ? (data as any).items
        : [];
    const isEnd = items.length < size;

    return (
        <Section
            title="Korisnici"
            right={
                <div className="grid gap-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-7 text-gray-900">
                    <Input label="Ime" value={form.firstName}
                           onChange={(e) => setForm({...form, firstName: e.target.value})}/>
                    <Input label="Prezime" value={form.lastName}
                           onChange={(e) => setForm({...form, lastName: e.target.value})}/>
                    <Input label="Email" type="email" autoComplete="email" value={form.email}
                           onChange={(e) => setForm({...form, email: e.target.value})}/>
                    <label className="grid gap-1 text-sm text-gray-700">
                        <span className="font-medium">Tip</span>
                        <select
                            className="h-11 w-full rounded-xl border border-gray-200 bg-white text-gray-900 px-3 focus:outline-none focus:ring-2 focus:ring-black/20"
                            value={form.type}
                            onChange={(e) => setForm({...form, type: e.target.value as any})}
                        >
                            <option value="EVENT_CREATOR">event creator</option>
                            <option value="ADMIN">admin</option>
                        </select>
                    </label>
                    <Input label="Lozinka" type="password" autoComplete="new-password" value={form.password}
                           onChange={(e) => setForm({...form, password: e.target.value})}/>
                    <Input label="Potvrda lozinke" type="password" autoComplete="new-password" value={form.password2}
                           onChange={(e) => setForm({...form, password2: e.target.value})}/>
                    <div className="flex items-end">
                        <Button className="w-full h-11 rounded-xl" variant="primary" onClick={add}>Dodaj</Button>
                    </div>
                </div>
            }
        >
            {loading && <div className="text-gray-700">Učitavanje…</div>}
            {error && <div className="text-red-600">{error}</div>}

            <div className="overflow-x-auto -mx-2 md:mx-0">
                <div className="min-w-full rounded-xl border border-gray-200 bg-white shadow-sm">
                    <table className="w-full text-sm text-gray-900">
                        <thead className="bg-gray-50 text-gray-700">
                        <tr className="[&>th]:py-3 [&>th]:px-3 border-b border-gray-200">
                            <th>Ime i prezime</th>
                            <th>Email</th>
                            <th>Tip</th>
                            <th>Status</th>
                            <th className="text-right">Akcije</th>
                        </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                        {(table.items as UserDTO[]).map((u) => (
                            <tr key={u.id} className="hover:bg-gray-50">
                                <td className="py-3 px-3">{u.firstName} {u.lastName}</td>
                                <td className="px-3">{u.email}</td>
                                <td className="px-3">{u.userType}</td>
                                <td className="px-3">{u.userStatus}</td>
                                <td className="px-3">
                                    <div className="flex justify-end gap-2">
                                        {u.userType !== "ADMIN" && (
                                            <>
                                                <Button size="sm" variant="primary"
                                                        onClick={() => activate(u, true)}>Aktiviraj</Button>
                                                <Button size="sm" variant="primary"
                                                        onClick={() => activate(u, false)}>Deaktiviraj</Button>
                                            </>
                                        )}
                                        <Button size="sm" variant="danger" onClick={() => del(u)}>Obriši</Button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {Array.isArray((data as any).items) && (
                <div className="mt-3">
                    <Pagination
                        page={page}
                        onPrev={() => setPage(Math.max(0, page - 1))}
                        onNext={() => setPage(page + 1)}
                        isEnd={isEnd}
                    />
                </div>
            )}
        </Section>
    );
}

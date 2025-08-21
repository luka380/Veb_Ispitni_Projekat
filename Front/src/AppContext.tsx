import React, {createContext, useContext, useEffect, useReducer, useState} from "react";
import {getUserInfo} from "./api.ts";
import type {UserDTO} from "./models.ts";

type State = { currentAccount: UserDTO | null };
type Action = { type: "setAccount"; payload: UserDTO | null } | { type: "clear" };

const AppCtx = createContext<{ state: State; dispatch: React.Dispatch<Action> } | null>(null);

function reducer(state: State, action: Action): State {
    switch (action.type) {
        case "setAccount":
            return {...state, currentAccount: action.payload};
        case "clear":
            return {...state, currentAccount: null};
        default:
            return state;
    }
}

export function AppProvider({children}: { children: React.ReactNode }) {
    const [state, dispatch] = useReducer(reducer, {currentAccount: null});
    return <AppCtx.Provider value={{state, dispatch}}>{children}</AppCtx.Provider>;
}

export function useApp() {
    const ctx = useContext(AppCtx);
    if (!ctx) throw new Error("useApp must be used within AppProvider");
    return ctx;
}

export function AppGate({children}: { children: React.ReactNode }) {
    const {dispatch} = useApp();
    const [ready, setReady] = useState(false);

    useEffect(() => {
        (async () => {
            try {
                const acc = await getUserInfo();
                dispatch({type: "setAccount", payload: acc});
            } finally {
                setReady(true);
            }
        })();
    }, [dispatch]);

    return ready ? <>{children}</> : <div>Loading…</div>;
}


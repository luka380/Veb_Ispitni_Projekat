import {useEffect, useState} from "react";
import {Pill, Section} from "./Components/SmallComponents.tsx";
import {LoginBox} from "./Components/Login.tsx";
import {PublicArea} from "./Public/Main.tsx";
import {EMS} from "./EMS/Main.tsx";
import {useApp} from "./AppContext.tsx";
import {UserType} from "./models.ts";
import {BASE} from "./api.ts";

export default function App() {
    const {state} = useApp();
    const [area, setArea] = useState<"public" | "ems">("public");

    useEffect(() => {
        function openFromAnywhere() {

        }

        window.addEventListener("open-event", openFromAnywhere as EventListener);
        return () => window.removeEventListener("open-event", openFromAnywhere as EventListener);
    }, []);

    return (
        <div className="min-h-[100svh] w-screen bg-gradient-to-b from-gray-50 to-white overflow-x-hidden flex flex-col">
            <header
                className="sticky top-0 z-20 bg-white/80 backdrop-blur supports-[backdrop-filter]:bg-white/60 border-b">
                <div className="w-full px-4 md:px-6 py-3 flex items-center gap-4">
                    <div className="text-lg md:text-xl font-bold tracking-tight">RAF Event Booker</div>
                    <nav className="flex items-center gap-2">
                        <Pill active={area === "public"} onClick={() => setArea("public")}>Javna platforma</Pill>
                        <Pill active={area === "ems"} onClick={() => setArea("ems")}>
                            <span className={state.currentAccount!.userType === UserType.ANONYMOUS ? "opacity-60" : ""}
                                  title={state.currentAccount!.userType === UserType.ANONYMOUS ? "Prijavite se" : "EMS"}>EMS</span>
                        </Pill>
                    </nav>
                    <div className="ml-auto"/>
                    <LoginBox/>
                </div>
            </header>

            <main className="flex-1 w-full px-4 md:px-6 py-6 md:py-10">
                {area === "public" ? <PublicArea/> : state.currentAccount!.userType === UserType.ANONYMOUS ?
                    <Section title="Pristup odbijen">Prijavite se za EMS.</Section> :
                    <EMS role={state.currentAccount!.userType}/>}
            </main>

            <footer className="py-10 text-center text-xs text-gray-500">Connected to {BASE}</footer>
        </div>
    );
}

import React, {useEffect, useState} from "react";
import {createPortal} from "react-dom";

export function Modal({
                          open,
                          onClose,
                          children,
                          center = true,
                      }: {
    open: boolean;
    onClose: () => void;
    children: React.ReactNode;
    center?: boolean;
}) {
    const [mounted, setMounted] = useState(false);

    useEffect(() => {
        setMounted(true);
    }, []);

    useEffect(() => {
        if (!open) return;
        const prev = document.body.style.overflow;
        document.body.style.overflow = "hidden";
        return () => {
            document.body.style.overflow = prev;
        };
    }, [open]);

    if (!open || !mounted) return null;

    return createPortal(
        <div className="fixed inset-0 z-[1000] pointer-events-auto">
            <div className="absolute inset-0 bg-black/50" onClick={onClose}/>
            <div
                className={`relative w-full min-h-[100svh] flex ${
                    center ? "items-center" : "items-start"
                } justify-center p-4 md:p-6 overflow-y-auto`}
            >
                {children}
            </div>
        </div>,
        document.body
    );
}
import {Button} from "./Button.tsx";

type PaginationKnownTotal = {
    page: number;
    onPrev: () => void;
    onNext: () => void;
    size: number;
    total: number;
    isEnd?: never;
};

type PaginationUnknownTotal = {
    page: number;
    onPrev: () => void;
    onNext: () => void;
    isEnd: boolean;
    size?: never;
    total?: never;
};

type PaginationProps = PaginationKnownTotal | PaginationUnknownTotal;

export function Pagination(props: PaginationProps) {
    const {page, onPrev, onNext} = props;

    const hasTotals = "size" in props && "total" in props;
    const lastPage = hasTotals
        ? Math.max(0, Math.ceil((props.total || 0) / (props.size || 10)) - 1)
        : undefined;

    const disablePrev = page <= 0;
    const disableNext = hasTotals ? page >= (lastPage as number) : props.isEnd;

    return (
        <div className="mt-6 flex items-center gap-3 text-white">
            <Button size="sm" variant="primary" disabled={disablePrev} onClick={onPrev} aria-label="Prethodna strana">
                Prev
            </Button>

            <div className="text-sm text-gray-600">
                {hasTotals ? (
                    <>Strana {page + 1} / {(lastPage as number) + 1}</>
                ) : (
                    <>Strana {page + 1}</>
                )}
            </div>

            <Button size="sm" variant="primary" disabled={disableNext} onClick={onNext} aria-label="Sledeća strana">
                Next
            </Button>
        </div>
    );
}